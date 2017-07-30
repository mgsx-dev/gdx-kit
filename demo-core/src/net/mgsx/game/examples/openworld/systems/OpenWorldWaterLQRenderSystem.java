package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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

@Storable(value="ow.water.lq")
@EditableSystem
public class OpenWorldWaterLQRenderSystem extends EntitySystem
{
	@Inject OpenWorldSkySystem sky;
	@Inject OpenWorldEnvSystem env;
	
	@ShaderInfo(vs="shaders/water-lq.vert", fs="shaders/water-lq.frag")
	public static class WaterShaderLQ extends ShaderProgramManaged
	{
		@Editable public float speed = 15;
		@Uniform transient float time;
		@Uniform @Editable public float frequency = 10;
		@Uniform @Editable public float amplitude = 0.005f;
		@Uniform @Editable public float transparency = 0.3f;
		@Uniform transient Vector3 camPos = new Vector3();
		@Uniform transient Cubemap texture;
	}
	
	@Editable public WaterShaderLQ waterShaderLQ = new WaterShaderLQ();
	
	
	private ShapeRenderer waterRenderer;
	
	private GameScreen screen;
	
	public OpenWorldWaterLQRenderSystem(GameScreen screen) {
		super(GamePipeline.RENDER);
		this.screen = screen;
	}
	
	@Override
	public void update(float deltaTime) {
		
		waterShaderLQ.time += deltaTime * waterShaderLQ.speed;
		waterShaderLQ.camPos.set(screen.camera.position);
		waterShaderLQ.texture = sky.getCubeMap();
		
		if(waterShaderLQ.begin()){
			if(waterRenderer != null) waterRenderer.dispose();
			waterRenderer = new ShapeRenderer(36, waterShaderLQ.program());
		}
		
		waterRenderer.setProjectionMatrix(screen.camera.combined);
		waterRenderer.begin(ShapeType.Filled);

		float s = screen.camera.far;
		
		waterRenderer.box(
				screen.camera.position.x - s, 
				-env.waterLevel, // TODO not minus !
				screen.camera.position.z - s, 
				s*2, 
				0, 
				-s*2); // TODO why negative depth ?
		
		waterRenderer.end();
		
		waterShaderLQ.end();
	}
	
}
