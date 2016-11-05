package net.mgsx.game.core.helpers.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

abstract public class ComponentIteratingSystem<T extends Component> extends IteratingSystem
{
	protected Class<T> type;
	protected ComponentMapper<T> mapper;

	public ComponentIteratingSystem(Class<T> type, int priority) {
		super(Family.one(type).get(), priority);
		this.type = type;
		mapper = ComponentMapper.getFor(type);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		T c = mapper.get(entity);
		if(c != null) processEntity(entity, c, deltaTime);
	}
	
	abstract protected void processEntity(Entity entity, T component, float deltaTime);
	
}