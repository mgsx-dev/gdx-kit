package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.states.WalkingState;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class WalkingAnimationSystem extends IteratingSystem
{

	public WalkingAnimationSystem() {
		super(Family.all(G3DModel.class, Box2DBodyModel.class, WalkingComponent.class, WalkingState.class).get(), GamePipeline.AFTER_LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(G3DModel.class, WalkingState.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(Entity entity) {
				G3DModel model = G3DModel.components.get(entity);
				WalkingComponent walk = WalkingComponent.components.get(entity);
				model.animationController.animate(walk.animation.id, .1f);
			}
		});
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		G3DModel model = G3DModel.components.get(entity);
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		WalkingComponent walk = WalkingComponent.components.get(entity);
		
		model.animationController.current.speed = physics.body.getLinearVelocity().x * walk.speedScale;
	
		if(physics.body.getLinearVelocity().x == 0){
			
		}
		
	}

}
