package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.TreesComponent;

@Storable(value="ow.water")
@EditableSystem
public class OpenWorldWaterRenderSystem extends EntitySystem
{
	@Inject OpenWorldLandRenderSystem landerRendering;
	@Inject OpenWorldEnvSystem environment;
	@Inject OpenWorldSkySystem sky;
	@Inject OpenWorldManagerSystem openWorldManager;
	
	@Editable public float frequency = 10;
	@Editable public float amplitude = 0.005f;
	@Editable public float transparency = 0.3f;
	@Editable public float speed = 1f;
	@Editable public float level = .3f;
	
	@Editable public boolean mirror = true;
	@Editable public int mirrorSize = 1024;

	private ShaderProgram waterShader, waterShaderMirror, waterShaderNoMirror, reflectionShader;
	private ShapeRenderer waterRenderer, waterRendererMirror, waterRendererNoMirror;
	
	private FrameBuffer mirrorBuffer;

	private GameScreen screen;
	
	public OpenWorldWaterRenderSystem(GameScreen screen) {
		super(GamePipeline.RENDER);
		this.screen = screen;
		
		loadShader();
	}
	
	@Editable
	public void loadShader(){
		
		ShaderProgram.prependVertexCode = ShaderProgram.prependFragmentCode = "#define CLIP_PLANE\n";
		if(reflectionShader != null) reflectionShader.dispose();
		reflectionShader = new ShaderProgram(
				Gdx.files.internal("shaders/land.vert"),
				Gdx.files.internal("shaders/land.frag"));
		if(!reflectionShader.isCompiled()){
			throw new GdxRuntimeException(reflectionShader.getLog());
		}
		
		ShaderProgram.prependVertexCode = ShaderProgram.prependFragmentCode = "#define MIRROR\n";
		if(waterShaderMirror != null) waterShaderMirror.dispose();
		waterShaderMirror = new ShaderProgram(
				Gdx.files.internal("shaders/water.vert"),
				Gdx.files.internal("shaders/water.frag"));
		if(!waterShaderMirror.isCompiled()){
			throw new GdxRuntimeException(waterShaderMirror.getLog());
		}
		
		ShaderProgram.prependVertexCode = ShaderProgram.prependFragmentCode = "";
		if(waterShaderNoMirror != null) waterShaderNoMirror.dispose();
		waterShaderNoMirror = new ShaderProgram(
				Gdx.files.internal("shaders/water.vert"),
				Gdx.files.internal("shaders/water.frag"));
		if(!waterShaderNoMirror.isCompiled()){
			throw new GdxRuntimeException(waterShaderNoMirror.getLog());
		}
		
		
		
		
		if(waterRendererMirror != null) waterRendererMirror.dispose();
		waterRendererMirror = new ShapeRenderer(36, waterShaderMirror);
		
		if(waterRendererNoMirror != null) waterRendererNoMirror.dispose();
		waterRendererNoMirror = new ShapeRenderer(36, waterShaderNoMirror);
		
		if(mirrorBuffer != null) mirrorBuffer.dispose();
		mirrorBuffer = new FrameBuffer(Format.RGBA8888, mirrorSize, mirrorSize, true);
		
		mirrorCamera = new PerspectiveCamera();
	}
	float time = 0;
	private Matrix4 backup = new Matrix4(), transform = new Matrix4();
	private PerspectiveCamera mirrorCamera;
	
	@Editable public boolean lands = true;
	@Editable public boolean trees = true;
	@Editable public boolean objects = true;
	
	@Override
	public void update(float deltaTime) {
		
		if(mirror){
			
			// render lands and trees from a mirrored view
			
			Gdx.gl.glEnable(GL20.GL_CULL_FACE);
			Gdx.gl.glCullFace(GL20.GL_FRONT);
			 
			// TODO setup camera
			backup.set(screen.camera.combined);
			
			mirrorCamera.position.set(screen.camera.position);
			mirrorCamera.position.y = -2*openWorldManager.scale *level - mirrorCamera.position.y;

			mirrorCamera.direction.set(screen.camera.direction);
			mirrorCamera.direction.y = -screen.camera.direction.y;
			mirrorCamera.near = screen.camera.near;
			mirrorCamera.far = screen.camera.far;
			mirrorCamera.up.set(screen.camera.up);

			mirrorCamera.viewportWidth = screen.camera.viewportWidth;
			mirrorCamera.viewportHeight = screen.camera.viewportHeight;
			mirrorCamera.fieldOfView = ((PerspectiveCamera)screen.camera).fieldOfView;
			
			mirrorCamera.update();
			
			screen.camera.combined.set(mirrorCamera.combined);
			
			// bind FBO
			mirrorBuffer.begin();
			Gdx.gl.glClearColor(0, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
			
			reflectionShader.begin();
			reflectionShader.setUniformMatrix("u_projTrans", screen.camera.combined);
			reflectionShader.setUniformMatrix("u_worldTrans", transform.idt());
			reflectionShader.setUniformf("u_sunDirection", environment.sunDirection);
			reflectionShader.setUniformf("u_fogColor", environment.fogColor);
			reflectionShader.setUniformf("u_clip", -openWorldManager.scale * level - 0.1f); // XXX offset for deformations.
			if(lands){
				for(Entity entity : getEngine().getEntitiesFor(Family.all(LandMeshComponent.class).get())){
					LandMeshComponent lmc = LandMeshComponent.components.get(entity);
					lmc.mesh.render(reflectionShader, GL20.GL_TRIANGLES);
				}
			}
			if(trees){
				for(Entity entity : getEngine().getEntitiesFor(Family.all(TreesComponent.class).get())){
					TreesComponent tmc = TreesComponent.components.get(entity);
					tmc.mesh.render(reflectionShader, GL20.GL_TRIANGLES);
				}
			}
			if(objects){
				for(Entity entity : getEngine().getEntitiesFor(Family.all(ObjectMeshComponent.class).get())){
					ObjectMeshComponent omc = ObjectMeshComponent.components.get(entity);
					transform.set(screen.camera.combined).mul(omc.getWorldTransform());
					reflectionShader.setUniformMatrix("u_worldTrans", omc.getWorldTransform());
					reflectionShader.setUniformMatrix("u_projTrans", transform);
					omc.getMeshToRender().render(reflectionShader, GL20.GL_TRIANGLES);
				}
			}
			reflectionShader.end();
			
			// unbind FBO
			mirrorBuffer.end();
			
			screen.camera.combined.set(backup);
		}
		
		time += deltaTime * speed;

		waterShader = mirror ? waterShaderMirror : waterShaderNoMirror;
		waterRenderer = mirror ? waterRendererMirror : waterRendererNoMirror;
		
		waterShader.begin();
		waterShader.setUniformf("u_time", time);
		waterShader.setUniformf("u_frequency", frequency);
		waterShader.setUniformf("u_amplitude", amplitude);
		waterShader.setUniformf("u_transparency", transparency);
		waterShader.setUniformi("u_texture", 0);
		waterShader.setUniformf("u_camPos", screen.camera.position);
		
		if(mirror){
			waterShader.setUniformi("u_mirrorTexture", 1);
			waterShader.setUniformf("u_window", new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
			mirrorBuffer.getColorBufferTexture().bind(1);
		}
		
		waterShader.end();
		
		waterRenderer.setProjectionMatrix(screen.camera.combined);
		waterRenderer.begin(ShapeType.Filled);
		//sky.getCubeMap().bind();
		Vector3 vOffset = Vector3.Zero; //screen.camera.position.cpy().scl(1);
		float s = 1e3f; // TODO not really that ... should be clipped to camera space ...
		
		
		waterRenderer.box(
				vOffset.x-s, 
				vOffset.y-openWorldManager.scale * level,
				vOffset.z-s, s*2, 0, -s*2);
		waterRenderer.end();
		
		if(mirror){
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + 1);
			Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		}

	}
}
