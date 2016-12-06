package net.mgsx.game.examples.platformer.inputs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.animations.WalkingComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;

public class WalkingSystem extends IteratingSystem
{
	public WalkingSystem() {
		super(Family.all(Box2DBodyModel.class, PlayerController.class, WalkingComponent.class).get(), GamePipeline.LOGIC);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PlayerController player = PlayerController.components.get(entity);
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		Vector2 velocity = physics.body.getLinearVelocity().cpy();
		float walkSpeed = 2;
		if(player.left){
			velocity.x = -walkSpeed;
		}else if(player.right){
			velocity.x = walkSpeed;
		}
		if(player.justJump){
			velocity.y = 7;
		}
		else if(player.jump){
			if(velocity.y < 0) velocity.y = -0.2f;
		}
		physics.body.setLinearVelocity(velocity);
		
		if(player.grab){
		}else{
			if(physics.body.getJointList().size > 0){
				physics.context.world.destroyJoint(physics.body.getJointList().get(0).joint);
			}
		}
	}

}
