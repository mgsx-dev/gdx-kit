package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.shaders.ShaderInfo;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged;
import net.mgsx.game.core.helpers.shaders.Uniform;
import net.mgsx.game.core.storage.SystemSettingsListener;

@Storable("ow.rain")
@EditableSystem
public class OpenWorldRainSystem extends EntitySystem implements SystemSettingsListener
{
	@Inject OpenWorldTimeSystem timeSystem;
	
	private Mesh mesh;
	private GameScreen screen;
	
	@Editable public int resolution = 64;
	
	@ShaderInfo(vs="shaders/rain.vert", fs="shaders/rain.frag")
	public static class RainShader extends ShaderProgramManaged 
	{
		@Editable @Uniform 
		public Color color = new Color(5f, .5f, 1f, .3f);
		
		@Editable @Uniform 
		public float size = 100;
		
		@Editable @Uniform("u_len") 
		public float length = 1;
		
		@Editable public float speed = 1;
		
		@Uniform transient protected float time;
		@Uniform transient protected Matrix4 projTrans;
		@Uniform transient protected Vector3 camPosition;
		
	}
	@Editable public RainShader rainShader = new RainShader();
	
	
	public OpenWorldRainSystem(GameScreen screen) {
		super(GamePipeline.RENDER_TRANSPARENT);
		this.screen = screen;
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
		int lineCount = resolution  * resolution;
		int vertexCount = lineCount * 2;
		int stride = 5;
		float [] vertices = new float[vertexCount * stride];
		// short indices = new short[lineCount * 2];
		
		int v = 0;
		for(int y=0 ; y<resolution ; y++){
			for(int x=0 ; x<resolution ; x++){
				vertices[v+0] = vertices[v+stride+0] = (float)x / (float)(resolution-1);
				vertices[v+1] = vertices[v+stride+1] = (float)y / (float)(resolution-1);
				vertices[v+2] = 0; vertices[v+stride+2] = 1;
				
				vertices[v+3] = vertices[v+stride+3] = (float)Math.random();
				vertices[v+4] = vertices[v+stride+4] = (float)Math.random();
				
				v += stride * 2;
			}
		}
		
		mesh = new Mesh(true, vertexCount, 0, VertexAttribute.Position(), VertexAttribute.TexCoords(0));
		mesh.setVertices(vertices);
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(rainShader.color.a <= 0) return;
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		// update values
		rainShader.time = timeSystem.time * rainShader.speed;
		rainShader.projTrans = screen.camera.combined;
		rainShader.camPosition = screen.camera.position;
		
		rainShader.begin();
		
		mesh.render(rainShader.program(), GL20.GL_LINES);
		
		rainShader.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	
}
