package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.RandomXS128;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.ShaderProgramHelper;
import net.mgsx.game.core.storage.SystemSettingsListener;

@Storable("ow.fauna")
@EditableSystem
public class OpenWorldFaunaSystem extends EntitySystem implements SystemSettingsListener
{
	@Inject OpenWorldTimeSystem timeSystem;
	
	private ShaderProgram shader;
	private Mesh mesh;
	private GameScreen screen;
	
	@Editable public int resolution = 64;
	@Editable public float speed = 1;
	
	public OpenWorldFaunaSystem(GameScreen screen) {
		super(GamePipeline.RENDER_TRANSPARENT);
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		loadShader();
		createMesh();
	}
	
	@Override
	public void onSettingsLoaded() {
		createMesh();
	}

	@Override
	public void beforeSettingsSaved() {
	}
	
	@Editable
	public void createMesh() {
		
		if(mesh != null) mesh.dispose();
		
		// create the mesh
		int quadCount = resolution  * resolution;
		int vertexCount = quadCount * 4;
		int stride = 5;
		float [] vertices = new float[vertexCount * stride];
		short [] indices = new short[quadCount * 6];
		
		for(int quad=0, qIndex=0, vIndex=0 ; quad<quadCount ; quad++, qIndex+=6, vIndex+=4){
			indices[qIndex+0] = (short)(vIndex+0);
			indices[qIndex+1] = (short)(vIndex+2);
			indices[qIndex+2] = (short)(vIndex+1);
			
			indices[qIndex+3] = (short)(vIndex+1);
			indices[qIndex+4] = (short)(vIndex+2);
			indices[qIndex+5] = (short)(vIndex+3);
		}
		
		RandomXS128 rand = new RandomXS128();
		
		int v = 0;
		for(int y=0 ; y<resolution ; y++){
			for(int x=0 ; x<resolution ; x++){
				float fx = (float)x / (float)(resolution-1);
				float fy = (float)y / (float)(resolution-1);
				float seed = rand.nextFloat();
				
				for(int dy=-1 ; dy<=1 ; dy+=2){
					for(int dx=-1 ; dx<=1 ; dx+=2){
						
						vertices[v+0] = fx;
						vertices[v+1] = fy;
						vertices[v+2] = seed;
						
						vertices[v+3] = dx;
						vertices[v+4] = dy;
						
						v += stride;
					}
				}
			}
		}
		
		mesh = new Mesh(true, vertexCount, indices.length, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
	}
	
	@Editable
	public void loadShader() {
		shader = ShaderProgramHelper.reload(shader, Gdx.files.internal("shaders/fauna.vert"), Gdx.files.internal("shaders/fauna.frag"));
	}

	@Override
	public void update(float deltaTime) 
	{
		// Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shader.begin();
		
//		Vector3 floorCamPos = new Vector3();
//		float s = resolution;
//		floorCamPos.x = MathUtils.floor(screen.camera.position.x / s) * s;
//		floorCamPos.y = MathUtils.floor(screen.camera.position.y / s) * s;
//		floorCamPos.z = MathUtils.floor(screen.camera.position.z / s) * s;
		
		// TODO maybe render 4 times around camera with different camera offset
		
		shader.setUniformMatrix("u_projTrans", screen.camera.combined);
		shader.setUniformf("u_camPosition", screen.camera.position);
		shader.setUniformf("u_time", timeSystem.time * speed);
		mesh.render(shader, GL20.GL_TRIANGLES);
		
		shader.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glDepthMask(true);
	}

	
}
