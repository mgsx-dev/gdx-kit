package net.mgsx.core.helpers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

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

		@Override
		protected void processEntity(Entity entity, float deltaTime) {
			processEntity(entity, entity.getComponent(type), deltaTime);
		}
		
		abstract protected void processEntity(Entity entity, T component, float deltaTime);
		
	}
	
}
