package net.mgsx.game.examples.platformer.core.states;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.plugins.fsm.systems.EntityStateSystem;
import net.mgsx.game.plugins.g3d.G3DModel;

public class FlyingSystem extends EntityStateSystem<FlyingState>
{
	public FlyingSystem() {
		super(FlyingState.class, configure().all(G3DModel.class));
	}

	@Override
	protected void enter(Entity entity) {
		entity.getComponent(G3DModel.class).animationController.paused = true;
		entity.getComponent(G3DModel.class).modelInstance.transform.setTranslation(0, 0, 0);
		state(entity).time = 0;
	}

	@Override
	protected void update(Entity entity, float deltaTime) {
		float time = state(entity).time += deltaTime;
		float t = time * 2.34f;
		entity.getComponent(G3DModel.class).modelInstance.transform.setTranslation(t, t, 0);
		if(t > 3){
			change(entity, EatState.class);
		}
	}

	@Override
	protected void exit(Entity entity) {
		entity.getComponent(G3DModel.class).animationController.paused = false;
	}
	

	


}
