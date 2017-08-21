package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.helpers.ShaderProgramHelper;
import net.mgsx.game.plugins.camera.model.POVModel;

@EditableSystem
public class OpenWorldLensFlareSystem extends EntitySystem
{
	@Inject OpenWorldEnvSystem environment;
	@Inject POVModel pov;
	
	private ShaderProgram shader;
	private ShapeRenderer renderer;
	
	public OpenWorldLensFlareSystem() {
		super(GamePipeline.RENDER_TRANSPARENT);
	}
	
	
	@Editable
	public void loadShaders(){
		
		shader = ShaderProgramHelper.reload(shader,
				Gdx.files.internal("shaders/flare.vert"),
				Gdx.files.internal("shaders/flare.frag"));
		
		if(renderer != null) renderer.dispose();
		renderer = new ShapeRenderer(36, shader);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		loadShaders();
	}
	
	Color[] colors = {Color.RED, Color.BLUE, Color.RED, Color.GREEN};

	@Override
	public void update(float deltaTime) 
	{
		
		int num = 4;
		for(int i=0 ; i<num ; i++){
			
			Color color = colors[i%colors.length];
			shader.begin();
			shader.setUniformf("u_sunDirection", environment.sunDirection);
			shader.setUniformf("u_camDirection", pov.camera.direction);
			shader.setUniformf("u_color", color);
			shader.setUniformf("u_size", (i+1) / (float)num);
			shader.setUniformf("u_ratio", Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight());
			// shader.setUniformf("u_camUp", screen.camera.up);
			shader.end();
			
			float h = 1; //Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
			float w = -h;
			
			renderer.setProjectionMatrix(pov.camera.combined);
			renderer.begin(ShapeType.Filled);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			
			renderer.rect(-w, -h, 2*w, 2*h);
			renderer.end();
		}
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
}
