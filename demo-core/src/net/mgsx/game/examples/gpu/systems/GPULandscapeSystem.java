package net.mgsx.game.examples.gpu.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.gpu.utils.MeshGenerator;
import net.mgsx.game.examples.gpu.utils.ShaderProgramEx;
import net.mgsx.game.plugins.g3d.components.DirectionalLightComponent;

@Storable("gpu.landscape")
@EditableSystem
public class GPULandscapeSystem extends EntitySystem
{
	private EditorScreen screen;

	private Mesh mesh;
	
	public ShaderProgram shader;
	
	@Editable
	public float height = 5;
	@Editable
	public float frequency = 0.2f;
	@Editable
	public float speed = 0.2f;
	@Editable
	public float length = 10f;
	@Editable
	public float texScale = 1;
	@Editable
	public float roughness = 1;
	@Editable
	public float roadScale = 1f;
	@Editable
	public float roadWidth = .1f;
	@Editable
	public float slices = 5;
	
	private int size = 30;
	
	private float time;
	
	private Matrix4 mat = new Matrix4();
	
	private static String prepareCode(FileHandle file)
	{
		String content = file.readString();
		String [] lines = content.split("\n");
		
		String result = ""; 
		
		for(String line : lines){
			if(line.startsWith("#include")){
				String inclusion = line.substring(8).trim();
				FileHandle dep = file.parent().child(inclusion + ".glsl");
				result += prepareCode(dep);
			}else{
				result += line + "\n";
			}
		}
		
		return result;
		
	}
	
	public GPULandscapeSystem(EditorScreen screen) {
		super(GamePipeline.RENDER);
		this.screen = screen;
		
		// TODO create static mesh
		mesh = MeshGenerator.grid(size);
		
		shader = new ShaderProgramEx(
				prepareCode(Gdx.files.internal("gpu/landscape.vs")), 
				prepareCode(Gdx.files.internal("gpu/landscape.gs")),
				prepareCode(Gdx.files.internal("gpu/landscape.fs")));
		if(!shader.isCompiled()){
			throw new GdxRuntimeException(shader.getLog());
		}
		
	}
	
	private Matrix3 normalMatrix = new Matrix3();
	
	@Override
	public void update(float deltaTime) {
		
		time += deltaTime * speed;
		mat.idt().rotate(Vector3.X, -90).translate(-size/2, -size/2, 0).mulLeft(screen.getGameCamera().combined);
		
		Vector3 pos = screen.getGameCamera().position;
		
		shader.begin();
		
		ImmutableArray<Entity> lights = getEngine().getEntitiesFor(Family.all(DirectionalLightComponent.class).get());
		if(lights.size() > 0){
			Entity e = lights.first();
			DirectionalLightComponent light = DirectionalLightComponent.components.get(e);
			shader.setUniformf("u_lightDir", light.light.direction.nor());
			shader.setUniformf("u_lightColor", light.light.color);
		}
		
		
		normalMatrix.set(screen.getGameCamera().view).inv().transpose();
		//shader.setUniformMatrix("u_normalMatrix", normalMatrix);
		
		shader.setUniformMatrix("u_projTrans", mat);
		shader.setUniformf("u_height", height);
		shader.setUniformf("u_time", time);
		shader.setUniformf("u_texScale", texScale);
		shader.setUniformf("u_roughness", roughness);
		shader.setUniformf("u_roadScale", roadScale);
		shader.setUniformf("u_roadWidth", roadWidth);
		shader.setUniformf("u_slices", slices);
		shader.setUniformf("u_eyePos", pos);
		
		// shader.setUniformf("u_length", length);
		shader.setUniformf("u_frequency", frequency);
		mesh.render(shader, GL20.GL_TRIANGLES);
		shader.end();
		// TODO bind matrix to shader ...
	}
	
}
