package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.shaders.ShaderInfo;
import net.mgsx.game.core.helpers.shaders.ShaderProgramManaged;
import net.mgsx.game.core.helpers.shaders.Uniform;
import net.mgsx.game.examples.openworld.model.OpenWorldRuntimeSettings;
import net.mgsx.game.plugins.camera.model.POVModel;

@Storable(value="ow.water.lq")
@EditableSystem
public class OpenWorldWaterLQRenderSystem extends EntitySystem implements PostInitializationListener
{
	@Inject OpenWorldSkySystem sky;
	@Inject OpenWorldEnvSystem env;
	@Inject POVModel pov;
	
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
	
	
	private ImmediateModeRenderer renderer;
	
	public OpenWorldWaterLQRenderSystem() {
		super(GamePipeline.RENDER);
	}
	
	@Override
	public void onPostInitialization() 
	{
		if(OpenWorldRuntimeSettings.highQuality){
			setProcessing(false);
		}else{
			setProcessing(true);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		
		waterShaderLQ.time += deltaTime * waterShaderLQ.speed;
		waterShaderLQ.camPos.set(pov.camera.position);
		waterShaderLQ.texture = sky.getCubeMap();
		
		if(waterShaderLQ.begin()){
			if(renderer != null) renderer.dispose();
			renderer = new ImmediateModeRenderer20(4, false, false, 0, waterShaderLQ.program());
		}
		
		renderer.begin(pov.camera.combined, GL20.GL_TRIANGLE_STRIP);

		float s = pov.camera.far;
		
		float x = pov.camera.position.x;
		float y = env.waterLevel;
		float z = pov.camera.position.z;
		
		renderer.vertex(x-s, y, z-s);
		renderer.vertex(x+s, y, z-s);
		renderer.vertex(x-s, y, z+s);
		renderer.vertex(x+s, y, z+s);
		
		renderer.end();
		
		waterShaderLQ.end();
		
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
	}
	
}
