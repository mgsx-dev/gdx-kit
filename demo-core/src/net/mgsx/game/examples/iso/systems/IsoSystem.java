package net.mgsx.game.examples.iso.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;

@Storable("iso")
@EditableSystem
public class IsoSystem extends EntitySystem
{
	private EditorScreen screen;

	@Editable
	public Vector3 light = new Vector3(0,-1,0);
	
	private ShaderProgram shader;
	private Mesh mesh;
	private Vector2 resolution = new Vector2();
	
	public IsoSystem(EditorScreen editor) {
		super(GamePipeline.RENDER);
		this.screen = editor;
		
		mesh = new Mesh(true, 4, 4,VertexAttribute.Position());
		float s = 0.9f;
		float [] vertices = new float[]{
				-s,s,0,
				s,s,0,
				-s,-s,0,
				s,-s,0};
		short [] indices = new short[]{0,1,2,3};
		
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		
		loadShader();
	}
	
	@Editable
	public void loadShader(){
		ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/iso.vs"), Gdx.files.internal("shaders/iso.fs"));
		if(shader.isCompiled()){
			if(this.shader != null){
				this.shader.dispose();
			}
			this.shader = shader;
		}else{
			System.err.println(shader.getLog());
			shader.dispose();
		}
	}
	
	public void update(float deltaTime) 
	{
		if(shader == null) return;
		
		shader.begin();
		
		Camera camera = screen.getGameCamera();
		
		shader.setUniformf("u_camPosition", camera.position);
		shader.setUniformf("u_camDirection", camera.direction);
		shader.setUniformf("u_resolution", resolution.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		shader.setUniformf("u_light", light.nor());
		
		mesh.render(shader, GL20.GL_TRIANGLE_STRIP);
		
		shader.end();
	}
}
