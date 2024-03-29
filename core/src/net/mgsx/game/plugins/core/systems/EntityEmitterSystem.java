package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.plugins.core.components.EntityEmitter;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class EntityEmitterSystem extends IteratingSystem
{
	final private GameScreen game;

	public EntityEmitterSystem(GameScreen game) {
		super(Family.all(EntityEmitter.class).get(), GamePipeline.LOGIC);
		this.game = game;
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
		
		for(Entity entityClone : emitter.template.create(game.assets, getEngine())){
			
			if(selfTransform != null){
				Transform2DComponent transform = Transform2DComponent.components.get(entityClone);
				if(transform != null){
					transform.position.add(selfTransform.position);
				}
			}
		}
		
	}
	
}
