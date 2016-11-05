package net.mgsx.game.core.helpers.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

public abstract class EventComponentIteratingSystem<T extends Component> extends ComponentIteratingSystem<T> implements EntityListener {

	public EventComponentIteratingSystem(Class<T> type, int priority) {
		super(type, priority);
	}

	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		engine.addEntityListener(getFamily(), this);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}
	
	@Override
	public void entityAdded(Entity entity) {
		entityAdded(entity, mapper.get(entity));
	}
	@Override
	public void entityRemoved(Entity entity) {
		entityRemoved(entity, (T)entity.remove(type)); // XXX doesn't work ...
	}
	
	abstract protected void entityAdded(Entity entity, T component);
	abstract protected void entityRemoved(Entity entity, T component);
}
