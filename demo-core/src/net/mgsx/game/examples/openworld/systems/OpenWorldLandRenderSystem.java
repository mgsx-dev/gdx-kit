package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.plugins.core.components.HeightFieldComponent;

@EditableSystem
public class OpenWorldLandRenderSystem extends IteratingSystem
{
	private VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal());
	private ShaderProgram shader;
	
	private GameScreen screen;
	
	public OpenWorldLandRenderSystem(GameScreen screen) {
		super(Family.all(LandMeshComponent.class).get(), GamePipeline.RENDER);
		this.screen = screen;
	}
	
	@Editable
	public void loadShaders() {
		if(shader != null) {
			shader.dispose();
		}
		shader = new ShaderProgram(
				Gdx.files.internal("shaders/land.vert"), 
				Gdx.files.internal("shaders/land.frag"));
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
	
	private void createMesh(Entity entity) {
		LandMeshComponent landMesh = LandMeshComponent.components.get(entity);
		HeightFieldComponent hfc = HeightFieldComponent.components.get(entity);
		
		int width = hfc.width;
		int height = hfc.height;
		
		// create the grid
		int verticesCount = width * height;
		int indicesCount = (width-1) * (height-1) * 6;
		int stride = attributes.vertexSize / (Float.SIZE / 8);
		float[] vertices = new float[verticesCount * stride];
		short[] indices = new short[indicesCount];
		
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
		
		Mesh mesh = new Mesh(true, verticesCount, indicesCount, attributes);
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		
		landMesh.mesh = mesh;
	}
	
	@Override
	public void update(float deltaTime) {
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		shader.begin();
		shader.setUniformMatrix("u_projTrans", screen.camera.combined);
		super.update(deltaTime);
		shader.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		LandMeshComponent lmc = LandMeshComponent.components.get(entity);
		lmc.mesh.render(shader, GL20.GL_TRIANGLES);
	}

}
