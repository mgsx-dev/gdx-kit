package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.EntityHelper;
import net.mgsx.game.plugins.core.components.EntityEmitter;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class EntityEmitterSystem extends IteratingSystem
{
	public EntityEmitterSystem() {
		super(Family.all(EntityEmitter.class).get(), GamePipeline.LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		EntityEmitter emitter = EntityEmitter.components.get(entity);
		if(emitter.running && (emitter.current < emitter.max || emitter.max < 0)){
			emitter.time += deltaTime;
			if(emitter.time > emitter.wait){
				emitter.time -= emitter.wait;
				
				emitter.current++;
				
				emit(entity, emitter);
			}
		}
		
	}
	
	private void emit(Entity self, EntityEmitter emitter)
	{
		Transform2DComponent selfTransform = Transform2DComponent.components.get(self);
		for(Entity entityTemplate : emitter.template){
			
			Entity entityClone = EntityHelper.clone(getEngine(), entityTemplate);
			
			if(selfTransform != null){
				Transform2DComponent transform = Transform2DComponent.components.get(entityClone);
				if(transform != null){
					transform.position.add(selfTransform.position);
				}
			}
			getEngine().addEntity(entityClone);
		}
		
	}
	
}
