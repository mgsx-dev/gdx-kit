package net.mgsx.game.examples.shmup.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.shmup.blueprint.Init;
import net.mgsx.game.examples.shmup.component.Enemy;
import net.mgsx.game.plugins.camera.model.POVModel;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class ShmupEnemySystem extends IteratingSystem
{
	@Inject public POVModel pov;
	
	public ShmupEnemySystem() {
		super(Family.all(Transform2DComponent.class, Enemy.class).get(), GamePipeline.LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Enemy enemy = Enemy.components.get(entity);
		
		if(enemy.current.size == 0){
			for(Init init : enemy.fsm.find(Init.class)){
				enemy.current.add(init);
			}
		}
		
		for(int i=0 ; i<enemy.current.size ; i++){
			enemy.current.get(i).update(getEngine(), entity, deltaTime);
		}
		
	}

}
