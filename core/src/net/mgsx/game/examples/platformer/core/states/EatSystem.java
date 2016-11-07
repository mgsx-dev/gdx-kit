package net.mgsx.game.examples.platformer.core.states;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.plugins.fsm.systems.EntityStateSystem;
import net.mgsx.game.plugins.g3d.G3DModel;

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
	}

	@Override
	protected void exit(Entity entity) {
		
	}

}
