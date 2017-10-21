package net.mgsx.game.examples.convoy.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.convoy.components.Planet;
import net.mgsx.game.examples.convoy.model.MaterialInfo;
import net.mgsx.game.examples.convoy.model.MaterialType;

public class PlanetLogicDeterministicSystem extends IteratingSystem
{
	float time = 0;
	
	public PlanetLogicDeterministicSystem() {
		super(Family.all(Planet.class).get(), GamePipeline.LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		final float period = 30;
		
		Planet planet = Planet.components.get(entity);
		
		for(Entry<MaterialType, MaterialInfo> mat : planet.materials){
			if(Math.abs(mat.value.stock) < 10){
				mat.value.fill += MathUtils.clamp(mat.value.target - mat.value.stock, -1, 1) * deltaTime / period;
				if(Math.abs(mat.value.fill) > 1){
					mat.value.stock += mat.value.fill > 0 ? 1 : -1;
					mat.value.fill = 0;
				}
			}
		}
	}
}
