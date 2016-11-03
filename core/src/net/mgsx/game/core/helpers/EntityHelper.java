package net.mgsx.game.core.helpers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * Helper for Ashley extension 
 */
public class EntityHelper 
{
	abstract public static class SingleComponentIteratingSystem<T extends Component> extends IteratingSystem
	{
		private Class<T> type;
		public SingleComponentIteratingSystem(Class<T> type) {
			super(Family.one(type).get());
			this.type = type;
		}

		public SingleComponentIteratingSystem(Class<T> type, int priority) {
			super(Family.one(type).get(), priority);
			this.type = type;
		}

		@Override
		protected void processEntity(Entity entity, float deltaTime) {
			T c = entity.getComponent(type); // TODO use mapper instead
			if(c != null) processEntity(entity, c, deltaTime);
		}
		
		abstract protected void processEntity(Entity entity, T component, float deltaTime);
		
	}
	
	public static Entity first(Engine engine, Family family){
		ImmutableArray<Entity> elements = engine.getEntitiesFor(family);
		return elements.size() > 0 ? elements.first() : null;
	}
	
}
