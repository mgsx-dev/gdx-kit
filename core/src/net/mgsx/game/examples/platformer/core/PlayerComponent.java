package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.plugins.box2d.Box2DListener;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.G3DModel;

public class PlayerComponent implements Component
{
	private G3DModel model;
	private Animation walkAnimation;
	private Animation idelAnimation;
	Box2DBodyModel physics;
	private static enum State{
		IDLE, WALK_RIGHT, WALK_LEFT
	}
	private State state;
	private boolean lookRight = false;
	private boolean onGround;
	private int contactCount = 0;
	
	public void initialize(Entity entity)
	{
		state = State.IDLE;
		
		physics = entity.getComponent(Box2DBodyModel.class);
		
		physics.fixtures.get(1).fixture.setUserData(new Box2DListener() { // XXX hard coded sensor index 1
			@Override
			public void endContact(Contact contact, Fixture self, Fixture other) {
				onGround = false;
				contactCount--;
				System.out.println(contactCount);
			}
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				onGround = true;
				contactCount++;
				System.out.println(contactCount);
			}
		});
		
		model = entity.getComponent(G3DModel.class);
		model.animationController.allowSameAnimation = true;
		walkAnimation = model.modelInstance.getAnimation("WalkCycle");
		idelAnimation = model.modelInstance.getAnimation("IdlePose");
		
	}
	
	private void updateControls(){
		Body body = physics.body;
		Vector2 vel = body.getLinearVelocity();
		float force = !onGround ? 1f : 5f;
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				body.applyForceToCenter(new Vector2(-force, 0),  true);
		}else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				body.applyForceToCenter(new Vector2(force, 0),  true);
		}else if(onGround){
			vel.x *= 0.9f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.Z)){
			if(onGround){
				vel.y = 6;
			}
		}
		float limit = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? 1.2f : 3.2f;
		float limity = 6.f;
		if(vel.x > limit) vel.x = limit;
		if(vel.x < -limit) vel.x = -limit;
		if(vel.y > limity) vel.y = limity;
		if(vel.y < -limity) vel.y = -limity;
		body.setLinearVelocity(vel);
	}
	
	public void update(float deltaTime)
	{
		onGround = contactCount >= 1; // TODO have to check at beginning : could already be in contact !
		
		updateControls();
		
		float duration = walkAnimation.duration;
		float off = 0; //duration * 4.f / 20.f;

		float animationSpeed = Math.abs(physics.body.getLinearVelocity().x) * 0.5f;
		// boolean onGround = Math.abs(physics.body.getLinearVelocity().y) < 0.001f;
		
		if(model.animationController != null && model.animationController.current != null){ // TODO only for walking animation (drawbacks for others!)
			model.animationController.current.speed = animationSpeed;
		}
		// TODO change transform (rotate 180 on Y Axis)
		if(onGround && state != State.WALK_LEFT && Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			state = State.WALK_LEFT;
			
			model.animationController.paused = false;
			model.animationController.setAnimation(walkAnimation.id, off, duration-off, -1, animationSpeed, null); // loop
			
			lookRight = false;
			
			System.out.println("moving left");
			
		}
		else if(onGround && state != State.WALK_RIGHT && Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			state = State.WALK_RIGHT;
			
			model.animationController.paused = false;
			model.animationController.setAnimation(walkAnimation.id, off, duration-off, -1, animationSpeed, null); // loop
			
			lookRight = true;
		}
		else if(state != State.IDLE && ((!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) || !onGround)){
			state = State.IDLE;
			model.animationController.paused = false;
			model.animationController.animate(idelAnimation.id, -1, 0.1f, null, 0.2f); // loop
			//model.animationController.setAnimation(idelAnimation.id, -1, 1, null); // loop
			//model.animationController.setAnimation(walkAnimation.id, off, 0.0001f, 0, -1, null);
//			model.origin.set(0,0,0);
			System.out.println("idle");
		}
		
		
		
		// TODO remove this, this a patch for model walk animation ...
		if(model.animationController != null && model.animationController.current != null){
			float t = model.animationController.current.time / model.animationController.current.duration;
			float delta = (t + off) * 1.4f;
			delta += 0.3f;
			if(lookRight) delta = -delta;
			
			
//			model.modelInstance.transform.setToTranslation(0, 0, 0);
//			model.modelInstance.transform.translate(delta, 0, 0);
			model.modelInstance.transform.setToTranslation(physics.body.getPosition().x, physics.body.getPosition().y -0.2f, -3.8f); // XXX hard coded offset from body ... 
//			model.modelInstance.transform.scale(0.5f, 0.5f, 0.5f);
			
			if(lookRight)
				model.modelInstance.transform.rotate(0, 1,0, 90);
			else
				model.modelInstance.transform.rotate(0, 1,0, -90);
			
		}

		
	}

	
}
