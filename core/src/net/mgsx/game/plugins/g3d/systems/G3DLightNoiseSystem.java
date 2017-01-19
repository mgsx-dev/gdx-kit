package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.math.ClassicNoise;
import net.mgsx.game.plugins.g3d.components.LightNoiseComponent;
import net.mgsx.game.plugins.g3d.components.PointLightComponent;

public class G3DLightNoiseSystem extends IteratingSystem
{
	private ClassicNoise noise = new ClassicNoise();
	private float time;
	
	public G3DLightNoiseSystem() {
		super(Family.all(PointLightComponent.class, LightNoiseComponent.class).get(), GamePipeline.BEFORE_RENDER);
	}
	
	@Override
	public void update(float deltaTime) {
		time += deltaTime;
		super.update(deltaTime);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		LightNoiseComponent noise = LightNoiseComponent.components.get(entity);
		PointLightComponent light = PointLightComponent.components.get(entity);
		
		float value = this.noise.apply(noise.offset + time * noise.frequency);
		float v = MathUtils.lerp(noise.min, noise.max, value);
		light.light.color.r = v;
		light.light.color.g = v;
		light.light.color.b = v;
		
	}
}
