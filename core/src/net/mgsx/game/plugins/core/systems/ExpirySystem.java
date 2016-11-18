package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.core.components.ExpiryComponent;

public class ExpirySystem extends IteratingSystem
{
	public ExpirySystem() {
		super(Family.all(ExpiryComponent.class).get(), GamePipeline.LAST);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ExpiryComponent expiry = ExpiryComponent.components.get(entity);
		expiry.time -= deltaTime;
		if(expiry.time <= 0) getEngine().removeEntity(entity);
	}

}
