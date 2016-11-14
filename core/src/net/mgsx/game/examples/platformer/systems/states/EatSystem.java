package net.mgsx.game.examples.platformer.systems.states;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.examples.platformer.components.EatState;
import net.mgsx.game.examples.platformer.components.FlyingState;
import net.mgsx.game.examples.platformer.components.PlayerController;
import net.mgsx.game.plugins.fsm.systems.EntityStateSystem;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class EatSystem extends EntityStateSystem<EatState>
{

	public EatSystem() {
		super(EatState.class, configure().all(G3DModel.class));
	}
	@Override
	protected void enter(Entity entity) {
		if(entity.getComponent(G3DModel.class).modelInstance.getAnimation("eat") != null)
			entity.getComponent(G3DModel.class).animationController.setAnimation("eat");
		else
			System.out.println("eat");
	}

	@Override
	protected void update(Entity entity, float deltaTime) {
		PlayerController player = PlayerController.components.get(entity);
		if(player != null && player.justGrab){
			player.justGrab = false;
			change(entity, FlyingState.class);
		}
	}

	@Override
	protected void exit(Entity entity) {
		
	}

}
