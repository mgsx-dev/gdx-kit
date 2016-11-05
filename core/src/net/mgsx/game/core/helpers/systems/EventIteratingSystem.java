package net.mgsx.game.core.helpers.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class EventIteratingSystem extends IteratingSystem implements EntityListener {

	private Family eventFamily;
	
	public EventIteratingSystem(Family eventFamily, Family iterateFamily, int priority) {
		super(iterateFamily, priority);
		this.eventFamily = eventFamily;
	}

	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		engine.addEntityListener(eventFamily, this);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}
	
	@Override
	public void entityAdded(Entity entity) {
	}
	@Override
	public void entityRemoved(Entity entity) {
	}
	
}
