package net.mgsx.game.examples.platformer.systems.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.components.PlayerController;
import net.mgsx.game.examples.platformer.components.states.EatState;
import net.mgsx.game.examples.platformer.components.states.FlyingState;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;

public class FlyingControlSystem extends IteratingSystem
{
	private static Vector2 force = new Vector2();
	
	public FlyingControlSystem() {
		super(Family.all(PlayerController.class, FlyingState.class, Box2DBodyModel.class).get(), GamePipeline.LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		PlayerController player = PlayerController.components.get(entity);
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		FlyingState state = FlyingState.components.get(entity);
		
		float scale = 60.f;
		
		physics.body.setGravityScale(1f);
		physics.body.setLinearDamping(5);
		
		state.directionChanged = false;
		if(player.left && !state.rightToLeft){
			state.rightToLeft = true;
			state.directionChanged = true;
		}else if(player.right && state.rightToLeft){
			state.rightToLeft = false;
			state.directionChanged = true;
		}
		
		force.set(state.rightToLeft ? -1 : 1, 0);
		force.setAngle(physics.body.getAngle() * MathUtils.radiansToDegrees);
		force.x += scale * 5;
		if(state.rightToLeft) force.x = -force.x;
		if(player.jump) force.y +=player.down ?-scale: scale;
		// player.getDirection(force);
		
		if(player.jump) state.wingsActivity = 1; else state.wingsActivity = 0;
		
		//force.x += state.rightToLeft ? -scale : scale;
		//force.y = 0;
		// player.getDirection(force).scl(scale * 5);
		//if(player.up) force.y = 0;
		if(player.jump) force.y *= 4.f;
		if(force.isZero()){
			
			// slow down
			// physics.body.setLinearVelocity(physics.body.getLinearVelocity().scl(-0.5f * deltaTime));
			// physics.body.setAngularVelocity(0);
			physics.body.setAngularVelocity(-physics.body.getAngle());
		}else{
			// move
			physics.body.applyForceToCenter(force, true);
			float target = 0;
			if(player.down){
				if(state.rightToLeft) target = 90; else target = -90;
			}else if(player.up){
				if(state.rightToLeft) target = -45; else target = 45;
			}
			
			float targetSpeed = player.jump ? 0.1f : 0.01f;
			
			physics.body.setAngularVelocity((target * MathUtils.degreesToRadians - physics.body.getAngle()) * targetSpeed / deltaTime);
//			float omega = 0;
//			if(player.jump){
//				if(state.rightToLeft)
//					omega = -180 *MathUtils.degreesToRadians - (float)Math.atan2(-force.y, -force.x) - physics.body.getAngle();
//				else
//					omega = (float)Math.atan2(force.y, force.x) - physics.body.getAngle();
//			}
//			if(player.jump)
//				physics.body.setAngularVelocity(-physics.body.getAngle());
//			else
//				physics.body.setAngularVelocity(omega);
		}
		// physics.body.setTransform(new Vector2(), 0);
		
		
		if(player.justGrab){
			player.justGrab = false;
			physics.body.setGravityScale(1f);
			physics.body.setLinearDamping(0);
			// physics.body.setLinearVelocity(0, 0);
			entity.remove(FlyingState.class);
			entity.add(getEngine().createComponent(EatState.class));
		}
		
	}
}
