package net.mgsx.game.examples.shmup.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.shmup.blueprint.Init;
import net.mgsx.game.examples.shmup.component.Emitter;
import net.mgsx.game.examples.shmup.component.Enemy;
import net.mgsx.game.plugins.camera.model.POVModel;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class ShmupEmitterSystem extends IteratingSystem
{
	@Inject public POVModel pov;
	

	public ShmupEmitterSystem() {
		super(Family.all(Transform2DComponent.class, Emitter.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Emitter emitter = Emitter.components.get(entity);
		Transform2DComponent emitterTransform = Transform2DComponent.components.get(entity);
		if(emitter.remains > 0 || emitter.count < 0){
			emitter.timeout -= deltaTime;
			if(emitter.timeout < 0){
				emitter.timeout = emitter.delay;
				emitter.remains--;
				
				// TODO create entity !
				Entity e = getEngine().createEntity();
				Transform2DComponent trans = getEngine().createComponent(Transform2DComponent.class);
				Enemy enemy = getEngine().createComponent(Enemy.class);
				
				trans.position.set(emitterTransform.position);
				
				enemy.fsm = emitter.fsm;
				for(Init init : enemy.fsm.find(Init.class)){
					enemy.current.add(init);
				}
				
				getEngine().addEntity(e.add(trans).add(enemy));
			}
		}
		
	}

}
