package net.mgsx.plugins.box2dold.behavior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class PlayerBehavior extends BodyBehavior
{
	private boolean jumping;
	
	@Override
	public void act() {
		Body body = bodyItem.body;
		Vector2 vel = body.getLinearVelocity();
		// Vector2 dir = new Vector2(vel).nor();
		boolean inTheAir = !worldItem.queryIsGroundTouch(body, new Vector2(0, -1), 100f); // TODO how ???
		if(!inTheAir) jumping = false;
		float force = inTheAir ? 0.5f : 1f;
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			// vel.x = -force;
//			if(vel.len() > 0.5f)
//				body.applyForceToCenter(new Vector2(dir).scl(-force),  true);
//			else
				body.applyForceToCenter(new Vector2(-force, 0),  true);
		}else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//			if(vel.len() > 0.5f)
//				body.applyForceToCenter(new Vector2(dir).scl(force),  true);
//			else
				body.applyForceToCenter(new Vector2(force, 0),  true);
		}
		;
		if(Gdx.input.isKeyPressed(Input.Keys.Z)){
			if(!jumping && !inTheAir){
				vel.y = 3;
				jumping = true;
			}
		}
		float limit = 2.2f;
		float limity = 6.f;
		if(vel.x > limit) vel.x = limit;
		if(vel.x < -limit) vel.x = -limit;
		if(vel.y > limity) vel.y = limity;
		if(vel.y < -limity) vel.y = -limity;
		body.setLinearVelocity(vel);
	}

}
