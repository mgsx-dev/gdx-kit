package net.mgsx.game.examples.platformer.core.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.fsm.systems.EntityStateSystem;
import net.mgsx.game.plugins.g3d.G3DModel;

public class FlyingSystem extends EntityStateSystem<FlyingState>
{
	public FlyingSystem() {
		super(FlyingState.class, configure().all(G3DModel.class));
	}

	@Override
	protected void enter(Entity entity) {
		G3DModel model = G3DModel.components.get(entity);
		model.animationController.paused = true;
		model.modelInstance.transform.setTranslation(0, 0, 0);
		model.animationController.setAnimation("birdFly", -1);
		state(entity).wingsActivity = 0;
	}

	@Override
	protected void update(Entity entity, float deltaTime) {
		G3DModel model = G3DModel.components.get(entity);
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		FlyingState state = FlyingState.components.get(entity);
		
		if(state.directionChanged){
			model.animationController.action("BirdSkeleton.001|backTurn", 1, 10, null, 0);
		}
		if(state.wingsActivity > 0 && !model.animationController.current.animation.id.equals("birdFly")){
			model.animationController.setAnimation("birdFly", -1);
		}
		else if(state.wingsActivity < 1 && model.animationController.current.animation.id.equals("birdFly")){
			model.animationController.animate("BirdSkeleton.001|BirdIdle", -1, null, 0.5f);
		}
		
		model.animationController.allowSameAnimation = true;
		// model.animationController.current.speed = 4;
		model.animationController.paused = false;
		if(physics != null){
			model.modelInstance.transform.setToTranslation(physics.body.getPosition().x, physics.body.getPosition().y, 0);
			model.modelInstance.transform.rotate(Vector3.Z, physics.body.getAngle() * MathUtils.radiansToDegrees);
			model.modelInstance.transform.rotate(Vector3.Y, state.rightToLeft ? -90 : 90);
			if(!model.animationController.inAction){
				if(physics.body.getLinearVelocity().y > 0)
					model.animationController.current.speed = physics.body.getLinearVelocity().y * 0.2f;
				else
					model.animationController.current.speed = 0;
			}else{
				model.animationController.current.speed = 0.5f;
			}
		}
	}

	@Override
	protected void exit(Entity entity) {
		entity.getComponent(G3DModel.class).animationController.paused = false;
	}
	

	


}
