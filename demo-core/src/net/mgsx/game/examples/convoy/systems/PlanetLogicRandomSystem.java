package net.mgsx.game.examples.convoy.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.convoy.components.Planet;
import net.mgsx.game.examples.convoy.model.MaterialType;

public class PlanetLogicRandomSystem extends IteratingSystem
{
	float time = 0;
	
	public PlanetLogicRandomSystem() {
		super(Family.all(Planet.class).get(), GamePipeline.LOGIC);
	}

	@Override
	public void update(float deltaTime) 
	{
		final float period = .3f;
		
		time += deltaTime;
		if(time > period) // TODO config
		{
			time -= period;
			
			int size = getEntities().size();
			Entity a = getEntities().get(MathUtils.random(size-1));
			Entity b = getEntities().get(MathUtils.random(size-1));
			MaterialType type = MaterialType.ALL[MathUtils.random(MaterialType.ALL.length-1)];
			Planet planetA = Planet.components.get(a);
			Planet planetB = Planet.components.get(b);
			if(Math.abs(planetA.materials.get(type).stock) < 10 && Math.abs(planetB.materials.get(type).stock) < 10 ){
				
				planetA.materials.get(type).stock += 1;
				planetB.materials.get(type).stock -= 1;
			}
		}
		super.update(deltaTime);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// Planet object = Planet.components.get(entity);
		
	}
}
