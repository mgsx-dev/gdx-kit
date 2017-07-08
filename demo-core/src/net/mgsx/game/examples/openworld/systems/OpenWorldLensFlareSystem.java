package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;

@EditableSystem
public class OpenWorldLensFlareSystem extends EntitySystem
{
	@Inject OpenWorldEnvSystem environment;
	
	private ShaderProgram shader;
	private ShapeRenderer renderer;
	
	private GameScreen screen;
	
	public OpenWorldLensFlareSystem(GameScreen screen) {
		super(GamePipeline.RENDER_TRANSPARENT);
		this.screen = screen;
	}
	
	
	@Editable
	public void loadShaders(){
		
		// XXX
		if(shader != null) shader.dispose();
		shader = new ShaderProgram(
				Gdx.files.internal("shaders/flare.vert"),
				Gdx.files.internal("shaders/flare.frag"));
		
		if(!shader.isCompiled()){
			throw new GdxRuntimeException(shader.getLog());
		}
		
		if(renderer != null) renderer.dispose();
		renderer = new ShapeRenderer(36, shader);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		loadShaders();
	}
	
	@Override
	public void update(float deltaTime) 
	{
		
		Color[] colors = {Color.RED, Color.BLUE, Color.RED, Color.GREEN};
		
		int num = 4;
		for(int i=0 ; i<num ; i++){
			
			Color color = colors[i%colors.length];
			shader.begin();
			shader.setUniformf("u_sunDirection", environment.sunDirection);
			shader.setUniformf("u_camDirection", screen.camera.direction);
			shader.setUniformf("u_color", color);
			shader.setUniformf("u_size", (i+1) / (float)num);
			shader.setUniformf("u_ratio", Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight());
			// shader.setUniformf("u_camUp", screen.camera.up);
			shader.end();
			
			float h = 1; //Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
			float w = h;
			
			renderer.setProjectionMatrix(screen.camera.combined);
			renderer.begin(ShapeType.Filled);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			
			renderer.rect(-w, -h, 2*w, 2*h);
			renderer.end();
		}
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
}
