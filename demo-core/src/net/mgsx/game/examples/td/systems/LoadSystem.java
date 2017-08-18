package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Load;

// TODO maybe load is intimly linked to shooter
public class LoadSystem extends IteratingSystem
{
	public LoadSystem() {
		super(Family.all(Load.class).get(), GamePipeline.LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Load load = Load.components.get(entity);
		load.reload -= deltaTime;
		if(load.reload < 0)
		{
			load.reload = 0;
		}
	}
}
