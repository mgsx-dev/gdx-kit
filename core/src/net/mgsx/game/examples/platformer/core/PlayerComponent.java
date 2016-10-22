package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.plugins.Initializable;
import net.mgsx.game.plugins.box2d.Box2DComponentListener;
import net.mgsx.game.plugins.box2d.Box2DComponentTrigger;
import net.mgsx.game.plugins.box2d.Box2DListener;
import net.mgsx.game.plugins.box2d.Box2DMultiplexer;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.G3DModel;

public class PlayerComponent implements Component, Initializable
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
	private Entity entity;
	private boolean climbing;
	private boolean canClimb;
	
	@Override
	public void initialize(final Engine engine, final Entity entity)
	{
		state = State.IDLE;
		
		this.entity = entity;
		
		physics = entity.getComponent(Box2DBodyModel.class);
		
		physics.fixtures.get(1).fixture.setUserData(new Box2DListener() { // XXX hard coded sensor index 1
			@Override
			public void endContact(Contact contact, Fixture self, Fixture other) {
				if(other.isSensor()) return;
				onGround = false;
				contactCount--;
				
				System.out.println(contactCount);
			}
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				if(other.isSensor()) return;
				onGround = true;
				contactCount++;
				System.out.println(contactCount);
				LogicComponent enemy = ((Entity)other.getBody().getUserData()).getComponent(LogicComponent.class);
				if(enemy != null){
					// physics.body.applyLinearImpulse(0, 1, 0, 0, true);
					// physics.body.setBullet(true);
					System.out.println("bounce!!");
					return;
				}
			}
		});
		
		Box2DListener bonusListener = new Box2DComponentListener<BonusComponent>(BonusComponent.class) {

			@Override
			protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, BonusComponent bonus) 
			{
				if(bonus.isCatchable()){
					bonus.setCatch();
					// TODO add score
					// remove bonus body (mark for deletion)
					// TODO should be done in bonus logic (how it disapear ...)
					Box2DBodyModel body = otherEntity.getComponent(Box2DBodyModel.class);
					body.context.scheduleRemove(otherEntity, body);
					contact.setEnabled(false);
					return;
				}
			}

			@Override
			protected void endContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, BonusComponent otherComponent) {
			}
		};
		
		Box2DListener enemyListener = new Box2DComponentListener<EnemyComponent>(EnemyComponent.class) {

			@Override
			protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, EnemyComponent enemy) 
			{
				contact.setRestitution(1f);
				// physics.body.applyLinearImpulse(0, 0.5f, 0, 0, true);
				System.out.println("hurt!!");
			}

			@Override
			protected void endContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, EnemyComponent otherComponent) {
			}
		};
		
		Box2DListener climbListener = new Box2DComponentTrigger<ClimbZone>(ClimbZone.class) {

			@Override
			protected void enter(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					ClimbZone otherComponent, boolean b) {
				canClimb = true;
			}

			@Override
			protected void exit(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					ClimbZone otherComponent, boolean b) {
				canClimb = false;
			}
		};
		
		physics.fixtures.get(0).fixture.setUserData(new Box2DMultiplexer(bonusListener, enemyListener, climbListener));
		
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
		float limity = 10.f;
		if(vel.x > limit) vel.x = limit;
		if(vel.x < -limit) vel.x = -limit;
		if(vel.y > limity) vel.y = limity;
		if(vel.y < -limity) vel.y = -limity;
		body.setLinearVelocity(vel);
	}
	
	public void update(float deltaTime)
	{
		entity.getComponent(Transform2DComponent.class).enabled = false; // XXX handle manually 
		
		onGround = contactCount >= 1; // TODO have to check at beginning : could already be in contact !

		// trigger climb mode
		if(!climbing && canClimb && Gdx.input.isKeyPressed(Input.Keys.UP)){
			climbing = true;
			
			model.animationController.animate("Climb", -1, 1, null, 1);
			physics.body.setGravityScale(0);
		}
		else if(climbing && !canClimb || climbing && Gdx.input.isKeyPressed(Input.Keys.Z) || climbing&&onGround&&!Gdx.input.isKeyPressed(Input.Keys.UP)){
			climbing = false;
			
			model.animationController.animate("IdlePose", -1, 1, null, 1);
			if(Gdx.input.isKeyPressed(Input.Keys.Z)) physics.body.applyLinearImpulse(0, 1f, // TODO jump force refactor ...
					0, 0, true); // XXX little jump not needed 
			physics.body.setGravityScale(1);
		}
		
		
		if(climbing)
			updateClimbing(deltaTime);
		else
			updateWalking(deltaTime);
	}
	private void updateClimbing(float deltaTime)
	{
		Body body = physics.body;
		Vector2 vel = body.getLinearVelocity();
		float speed = 2;
		
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			vel.x = -speed;
		}else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			vel.x = speed;
		}else{
			vel.x = 0;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			vel.y = speed;
		}else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			vel.y = -speed;
		}else{
			model.animationController.paused = true;
			vel.y = 0;
		}
		model.animationController.current.speed = 2.5f;
		if(vel.y != 0 || vel.x != 0) model.animationController.paused = false;
		
		body.setLinearVelocity(vel);
		
		// TODO refactor with walk state ?
		if(model.animationController != null && model.animationController.current != null){
			model.modelInstance.transform.setToTranslation(physics.body.getPosition().x, physics.body.getPosition().y -0.2f, 0); // XXX hard coded offset from body ... 
			model.modelInstance.transform.rotate(0, 1,0, 180);
			
		}
	}
	private void updateWalking(float deltaTime)
	{
		
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
			model.modelInstance.transform.setToTranslation(physics.body.getPosition().x, physics.body.getPosition().y -0.2f, 0); // XXX hard coded offset from body ... 
//			model.modelInstance.transform.scale(0.5f, 0.5f, 0.5f);
			
			if(lookRight)
				model.modelInstance.transform.rotate(0, 1,0, 90);
			else
				model.modelInstance.transform.rotate(0, 1,0, -90);
			
		}

		
	}

	
}
