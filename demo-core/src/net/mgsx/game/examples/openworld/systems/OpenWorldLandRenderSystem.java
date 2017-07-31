package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ShaderProgramHelper;
import net.mgsx.game.examples.openworld.components.CellDataComponent;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.components.WildLifeComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldPool;
import net.mgsx.game.examples.openworld.model.OpenWorldRuntimeSettings;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Storable("ow.lands")
@SuppressWarnings("deprecation")
@EditableSystem
public class OpenWorldLandRenderSystem extends IteratingSystem
{
	@Inject OpenWorldEnvSystem environment;
	@Inject OpenWorldSkySystem sky;
	
	private ShaderProgram shader;
	private ShaderProgram shaderHigh, shaderHighShadows, shaderHighNoShadows, objectsShader, depthShader;
	
	private GameScreen screen;
	
	private DirectionalShadowLight shadowLight;
	
	private ModelBatch shadowBatch;
	
	@Editable public boolean shadowEnabled = false;
	@Editable public int shadowMapSize = 1024;
	@Editable public float shadowViewportWidth = 500;
	@Editable public float shadowViewportHeight = 500;
	@Editable public float shadowNear = 1;
	@Editable public float shadowFar = 500;
	@Editable public float shadowPCFOffset = 0.0001f;
	
	public OpenWorldLandRenderSystem(GameScreen screen) {
		super(Family.all(LandMeshComponent.class).get(), GamePipeline.RENDER);
		this.screen = screen;
	}
	
	@Editable
	public void loadShaders() {
		shader = ShaderProgramHelper.reload(shader,
				Gdx.files.internal("shaders/land.vert"), 
				Gdx.files.internal("shaders/land.frag"));
		
		ShaderProgram.prependVertexCode = ShaderProgram.prependFragmentCode = "";
		shaderHighNoShadows = ShaderProgramHelper.reload(shaderHighNoShadows,
				Gdx.files.internal("shaders/land-high.vert"), 
				Gdx.files.internal("shaders/land-high.frag"));
		
		ShaderProgram.prependVertexCode = ShaderProgram.prependFragmentCode = "#define SHADOWS\n";
		shaderHighShadows = ShaderProgramHelper.reload(shaderHighShadows,
				Gdx.files.internal("shaders/land-high.vert"), 
				Gdx.files.internal("shaders/land-high.frag"));
		
		
		objectsShader = ShaderProgramHelper.reload(objectsShader,
				Gdx.files.internal("shaders/object-high.vert"), 
				Gdx.files.internal("shaders/object-high.frag"));
		
		depthShader = ShaderProgramHelper.reload(depthShader,
			Gdx.files.internal("shaders/depth.vert"), 
			Gdx.files.internal("shaders/depth.frag"));
		
		if(shadowBatch != null) shadowBatch.dispose();
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		
		if(shadowLight != null) shadowLight.dispose();
		shadowLight = new DirectionalShadowLight(shadowMapSize, shadowMapSize, shadowViewportWidth, shadowViewportHeight, shadowNear, shadowFar);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		// auto add a mesh 
		engine.addEntityListener(Family.all(HeightFieldComponent.class, LandMeshComponent.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(Entity entity) {
				createMesh(entity);
			}
		});
		
		loadShaders();
	}
	
	private float[] vertices;
	private short[] indices;
	
	private void createMesh(Entity entity) {
		LandMeshComponent landMesh = LandMeshComponent.components.get(entity);
		HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
		
		int width = hfc.width;
		int height = hfc.height;
		
		// create the grid
		int verticesCount = width * height;
		int indicesCount = (width-1) * (height-1) * 6;
		Mesh mesh = OpenWorldPool.landMeshPool.obtain(verticesCount, indicesCount);
		int stride = mesh.getVertexAttributes().vertexSize / (Float.SIZE / 8);
		int verticeBufferSize = verticesCount * stride;
		
		if(vertices == null || vertices.length < verticeBufferSize){
			vertices = new float[verticeBufferSize];
		}
		if(indices == null || indices.length < indicesCount){
			indices = new short[indicesCount];
		}
		
		// vertices
		for(int y=0 ; y<height ; y++) {
			for(int x=0 ; x<width ; x++) {
				int hIndex = (y*width + x);
				int index = hIndex * stride;
				vertices[index + 0] = hfc.position.x + x; // TODO * worldWidth;
				vertices[index + 1] = hfc.position.y + hfc.values[hIndex];
				vertices[index + 2] = hfc.position.z + y; // TODO * worldHeight;
				
				// normals
				Vector3 normal = hfc.normals[hIndex];
				vertices[index + 3] = normal.x;
				vertices[index + 4] = normal.z;
				vertices[index + 5] = normal.y;
				
				// texCoord
				vertices[index + 6] = (float)x / (float)width;
				vertices[index + 7] = (float)y / (float)height;
			}
		}
		
		// indices
		int iIndex = 0;
		for(int y=0 ; y<height-1 ; y++) {
			for(int x=0 ; x<width-1 ; x++) {
				indices[iIndex + 0] = (short)(y*width + x);
				indices[iIndex + 4] = 
				indices[iIndex + 1] = (short)(y*width + x+1);
				indices[iIndex + 3] = 
				indices[iIndex + 2] = (short)((y+1)*width + x);
				indices[iIndex + 5] = (short)((y+1)*width + x+1);
				iIndex += 6;
			}
		}
		
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		
		landMesh.mesh = mesh;
	}
	
	@Override
	public void update(float deltaTime) {
		if(OpenWorldRuntimeSettings.highQuality){
			renderHigh();
		}else{
			renderLow();
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// NOOP
	}
	
	public void renderLow() {
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		shader.begin();
		shader.setUniformMatrix("u_projTrans", screen.camera.combined);
		shader.setUniformf("u_sunDirection", environment.sunDirection);
		shader.setUniformf("u_fogColor", environment.fogColor);
		for(Entity entity : getEntities()){
			LandMeshComponent lmc = LandMeshComponent.components.get(entity);
			lmc.mesh.render(shader, GL20.GL_TRIANGLES);
		}
		shader.end();
	}
	
	Matrix4 transform = new Matrix4();
	
	public void renderHigh() {
		
		if(shadowEnabled){
			shadowLight.color.set(Color.RED); // XXX debug purpose
			shadowLight.direction.set(environment.sunDirection);
			shadowLight.getCamera().near = shadowNear;
			shadowLight.getCamera().far = shadowFar;
			shadowLight.getCamera().viewportWidth = shadowViewportWidth;
			shadowLight.getCamera().viewportHeight = shadowViewportHeight;
			
			shadowLight.begin(screen.camera.position, screen.camera.direction);
			
			// FrameBuffer.unbind();
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST); // XXX
			depthShader.begin();
			depthShader.setUniformMatrix("u_projViewWorldTrans", shadowLight.getCamera().combined);
			
			for(Entity entity : getEntities()){
				LandMeshComponent lmc = LandMeshComponent.components.get(entity);
				lmc.mesh.render(depthShader, GL20.GL_TRIANGLES);
			}
			for(Entity entity : getEngine().getEntitiesFor(Family.all(TreesComponent.class).get())){
				TreesComponent tmc = TreesComponent.components.get(entity);
				tmc.mesh.render(depthShader, GL20.GL_TRIANGLES);
			}
			
			for(Entity entity : getEngine().getEntitiesFor(Family.all(ObjectMeshComponent.class).get())){
				ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
				transform.set(shadowLight.getCamera().combined).mul(omc.getWorldTransform());
				depthShader.setUniformMatrix("u_projViewWorldTrans", transform);
				omc.getMeshToRender().render(depthShader, GL20.GL_TRIANGLES);
			}
			
			depthShader.end();
			
			
			shadowBatch.begin(shadowLight.getCamera());
			
			for(Entity entity : getEngine().getEntitiesFor(Family.all(WildLifeComponent.class, G3DModel.class).get())){
				G3DModel model = G3DModel.components.get(entity);
				shadowBatch.render(model.modelInstance);
			}
			shadowBatch.end();
			
			
			shadowLight.end();
		}
		
//		if(sb == null) sb = new SpriteBatch();
//		sb.disableBlending();
//		sb.begin();
//		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
//		sb.draw(shadowLight.getFrameBuffer().getColorBufferTexture(), 0, 0);
//		sb.end();
		
		// and the final scene
		renderHighFinal();
	}
	SpriteBatch sb;
	
	private void renderHighFinal()
	{
		shaderHigh = shadowEnabled ? shaderHighShadows : shaderHighNoShadows;
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		shaderHigh.begin();
		shaderHigh.setUniformMatrix("u_projTrans", screen.camera.combined);
		shaderHigh.setUniformf("u_sunDirection", environment.sunDirection);
		shaderHigh.setUniformf("u_fogColor", environment.fogColor);
		shaderHigh.setUniformf("u_waterColor", environment.waterColor);
		shaderHigh.setUniformf("u_waterLevel", environment.waterLevel);
		shaderHigh.setUniformf("u_camDirection", screen.camera.direction);
		shaderHigh.setUniformf("u_camPosition", screen.camera.position);
		
		if(shadowEnabled){
			shadowLight.getFrameBuffer().getColorBufferTexture().bind(1);
			shaderHigh.setUniformi("u_shadowTexture", 1);
			shaderHigh.setUniformMatrix("u_shadowMapProjViewTrans", shadowLight.getCamera().combined);
			shaderHigh.setUniformf("u_shadowPCFOffset", shadowPCFOffset); // TODO
		}
		
		shaderHigh.setUniformi("u_infoTexture", 2);
		for(Entity entity : getEntities()){
			LandMeshComponent lmc = LandMeshComponent.components.get(entity);
			CellDataComponent cdc = CellDataComponent.components.get(entity);
			cdc.data.infoTexture.bind(2);
			lmc.mesh.render(shaderHigh, GL20.GL_TRIANGLES);
		}
		shaderHigh.end();
		
		if(shadowEnabled){
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 1);
			Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
		}
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 2);
		Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		ShaderProgram shader = objectsShader;
		
		// render objects
		shader.begin();
		shader.setUniformf("u_sunDirection", environment.sunDirection);
		shader.setUniformf("u_fogColor", environment.fogColor);
		shader.setUniformf("u_camPosition", screen.camera.position);
		sky.getCubeMap().bind();
		for(Entity entity : getEngine().getEntitiesFor(Family.all(ObjectMeshComponent.class).get())){
			ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
			render(omc.getInstance().nodes, omc.getInstance().transform, shader);
			
//			transform.set(screen.camera.combined).mul(omc.getWorldTransform());
//			shader.setUniformf("u_color", omc.userObject.element.color);
//			shader.setUniformMatrix("u_projTrans", transform);
//			shader.setUniformMatrix("u_worldTrans", omc.getWorldTransform());
//			omc.getMeshToRender().render(shader, GL20.GL_TRIANGLES);
		}
		shader.end();
		
	}
	
	private Matrix4 worldTransform = new Matrix4();
	
	private void render(Iterable<Node> nodes, Matrix4 rootTransform, ShaderProgram shader) {
		for(Node node : nodes){
			worldTransform.set(rootTransform).mul(node.globalTransform);
			// setup matrices
			transform.set(screen.camera.combined).mul(worldTransform);
			shader.setUniformMatrix("u_projTrans", transform);
			shader.setUniformMatrix("u_worldTrans", worldTransform);
			
			for(NodePart part : node.parts){
				MeshPart meshPart = part.meshPart;
				// color from material
				ColorAttribute diffuse = (ColorAttribute)part.material.get(ColorAttribute.Diffuse);
				shader.setUniformf("u_color", diffuse.color);
				part.meshPart.mesh.render(shader, meshPart.primitiveType, meshPart.offset, meshPart.size);
			}
			render(node.getChildren(), rootTransform, shader);
		}
	}
	

}
