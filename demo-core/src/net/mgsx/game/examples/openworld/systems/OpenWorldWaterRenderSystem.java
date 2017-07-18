package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;

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

	private ShaderProgram waterShader;
	private ShapeRenderer waterRenderer;

	private GameScreen screen;
	
	public OpenWorldWaterRenderSystem(GameScreen screen) {
		super(GamePipeline.RENDER);
		this.screen = screen;
		
		loadShader();
	}
	
	@Editable
	public void loadShader(){
		if(waterShader != null) waterShader.dispose();
		waterShader = new ShaderProgram(
				Gdx.files.internal("shaders/water.vert"),
				Gdx.files.internal("shaders/water.frag"));
		
		if(!waterShader.isCompiled()){
			throw new GdxRuntimeException(waterShader.getLog());
		}
		
		if(waterRenderer != null) waterRenderer.dispose();
		waterRenderer = new ShapeRenderer(36, waterShader);
	}
	float time = 0;
	@Override
	public void update(float deltaTime) {
		
		time += deltaTime * speed;

		waterShader.begin();
		waterShader.setUniformf("u_time", time);
		waterShader.setUniformf("u_frequency", frequency);
		waterShader.setUniformf("u_amplitude", amplitude);
		waterShader.setUniformf("u_transparency", transparency);
		waterShader.setUniformi("u_texture", 0);
		waterShader.setUniformf("u_camPos", screen.camera.position);
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
		

	}
}
