package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Initializable;
import net.mgsx.game.core.components.LogicComponent;
import net.mgsx.game.examples.platformer.systems.PlatformerHUDSystem;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.listeners.Box2DAdapter;
import net.mgsx.game.plugins.box2d.listeners.Box2DComponentListener;
import net.mgsx.game.plugins.box2d.listeners.Box2DComponentTrigger;
import net.mgsx.game.plugins.box2d.listeners.Box2DListener;
import net.mgsx.game.plugins.box2d.listeners.Box2DMultiplexer;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.pd.Pd;

@Storable("example.platformer.player")
@EditableComponent(name="Player Logic",all={Box2DBodyModel.class, G3DModel.class})
public class PlayerComponent implements Component, Initializable
{
	private G3DModel model;
	private Animation walkAnimation;
	private Animation idelAnimation;
	public Box2DBodyModel physics;
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
	private boolean inWater, onLiana;
	
	private Body currentLiana;
	
	
	// Physics constants
	private float walkForceOnGround = 5f;
	private float walkForceInTheAir = 2f;
	private float jumpVelocity = 6f;
	private float jumpForce = 1f;
	private float lianaForce = 1;
	private float swimForce = 4; // TODO use speed instead of force (mass independant)
	private float climbSpeed = 2;
	

	
	@Override
	public void initialize(final Engine engine, final Entity entity)
	{
		state = State.IDLE;
		
		this.entity = entity;
		
		physics = entity.getComponent(Box2DBodyModel.class);
		
		if(physics.fixtures.size < 2) return; // XXX hard coded sensor index 1
		physics.fixtures.get(1).fixture.setUserData(new Box2DAdapter() { 
			@Override
			public void endContact(Contact contact, Fixture self, Fixture other) {
				if(other.isSensor()) return;
				onGround = false;
				contactCount--;
				if(contactCount == 0){
					Pd.audio.sendFloat("ground-out", MathUtils.clamp(self.getBody().getLinearVelocity().len() / 4.f, 0, 1));
				}
			}
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				if(other.isSensor()) return;
				onGround = true;
				if(contactCount == 0){
					Pd.audio.sendFloat("ground-in", MathUtils.clamp(self.getBody().getLinearVelocity().len() / 4.f, 0, 1));
				}
				contactCount++;
				System.out.println(contactCount);
				LogicComponent enemy = ((Entity)other.getBody().getUserData()).getComponent(LogicComponent.class);
				if(enemy != null){
					// physics.body.applyLinearImpulse(0, 1, 0, 0, true);
					// physics.body.setBullet(true);
					return;
				}
			}
		});
		
		Box2DListener bonusListener = new Box2DComponentListener<BonusComponent>(BonusComponent.class) {

			@Override
			protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity, BonusComponent bonus) 
			{
				if(bonus.isCatchable()){
					bonus.setCatch(otherEntity);
					// TODO add score from bonus settings, call game state machine instead of HUD !
					engine.getSystem(PlatformerHUDSystem.class).addScore(1000);
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
				enemy.alive = false; // TODO call methods on enemy instead
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
		
		Box2DListener swimListener = new Box2DComponentTrigger<WaterZone>(WaterZone.class) {

			@Override
			protected void enter(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					WaterZone otherComponent, boolean b) {
				inWater = true;
				enterWater();
			}

			@Override
			protected void exit(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					WaterZone otherComponent, boolean b) {
				inWater = false;
				exitWater();
			}
		};
		
		Box2DListener lianaListener = new Box2DComponentTrigger<LianaZone>(LianaZone.class) {

			@Override
			protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					LianaZone otherComponent) {
				if(!onLiana) currentLiana = otherEntity.getComponent(Box2DBodyModel.class).body;
				super.beginContact(contact, self, other, otherEntity, otherComponent);
			}
			@Override
			protected void enter(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					LianaZone otherComponent, boolean b) {
				enterLiana();
			}

			@Override
			protected void exit(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					LianaZone otherComponent, boolean b) {
				exitLiana();
			}
		};
		
		Box2DListener platformListener = new Box2DComponentListener<PlatformComponent>(PlatformComponent.class) {

			@Override
			protected void beginContact(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					PlatformComponent otherComponent) {
				contact.setFriction(1); // TODO is it really necessary ?
			}

			@Override
			protected void endContact(Contact contact, Fixture self, Fixture other, Entity otherEntity,
					PlatformComponent otherComponent) {
				
			}

		};
		

		physics.fixtures.get(0).fixture.setUserData(new Box2DMultiplexer(platformListener, lianaListener, bonusListener, enemyListener, climbListener, swimListener));
		
		model = entity.getComponent(G3DModel.class);
		model.animationController.allowSameAnimation = true;
		walkAnimation = model.modelInstance.getAnimation("WalkCycle");
		idelAnimation = model.modelInstance.getAnimation("IdlePose");
		
	}
	
	private void enterWater(){
		// TODO anim particle ...
		physics.body.setGravityScale(-0.1f);
		physics.body.setLinearDamping(2);
		model.animationController.animate("Climb", -1, 1, null, 1); // TODO swim animation
	}
	
	private void exitWater(){
		// TODO anim particle ...
		physics.body.setGravityScale(1);
		physics.body.setLinearDamping(0);
		model.animationController.animate("IdlePose", -1, 1, null, 1);
	}
	
	private void enterLiana(){
	}
	
	private void exitLiana(){
		currentLiana = null;
	}
	
	
	private void updateControls(){
		Body body = physics.body;
		Vector2 vel = body.getLinearVelocity();
		float force = !onGround ? walkForceInTheAir : walkForceOnGround;
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				body.applyForceToCenter(new Vector2(-force, 0),  true);
		}else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				body.applyForceToCenter(new Vector2(force, 0),  true);
		}else if(onGround){
			vel.x *= 0.9f; // TODO slow down ... should be handled by friction ?
		}
		if(Gdx.input.isKeyPressed(Input.Keys.Z)){
			if(onGround){
				vel.y = jumpVelocity;
			}
		}
		float limit = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ? 1.2f : 3.2f;
		float limity = 10.f;
		if(vel.x > limit) vel.x = limit;
		if(vel.x < -limit) vel.x = -limit;
		if(vel.y > limity) vel.y = limity;
		if(vel.y < -limity) vel.y = -limity;
		body.setLinearVelocity(vel); // TODO this causing sliding on moving objects instead of sticking ...
	}
	
	public void update(float deltaTime)
	{
		entity.getComponent(Transform2DComponent.class).enabled = false; // XXX handle manually 
		
		onGround = contactCount >= 1; // TODO have to check at beginning : could already be in contact !

		if(!onLiana && currentLiana!=null && Gdx.input.isKeyPressed(Input.Keys.A)){
			onLiana = true;
		}
		
		if(inWater)
		{
			updateSwim();
		}
		else if(onLiana)
		{
			updateLiana();
		}
		// trigger climb mode
		else if(!climbing && canClimb && Gdx.input.isKeyPressed(Input.Keys.UP)){
			climbing = true;
			
			model.animationController.animate("Climb", -1, 1, null, 1);
			physics.body.setGravityScale(0);
		}
		else if(climbing && !canClimb || climbing && Gdx.input.isKeyPressed(Input.Keys.Z) || climbing&&onGround&&!Gdx.input.isKeyPressed(Input.Keys.UP)){
			climbing = false;
			
			model.animationController.animate("IdlePose", -1, 1, null, 1);
			if(Gdx.input.isKeyPressed(Input.Keys.Z)) physics.body.applyLinearImpulse(0, jumpForce, 
					0, 0, true); // XXX little jump not needed 
			physics.body.setGravityScale(1);
		}
		
		if(inWater);
		else if(onLiana);
		else if(climbing)
			updateClimbing(deltaTime);
		else
			updateWalking(deltaTime);
	}
	
	
	private void updateLiana()
	{
		if(!Gdx.input.isKeyPressed(Input.Keys.A)){
			onLiana = false;
			physics.body.setTransform(physics.body.getPosition(), 0);
			physics.body.setLinearVelocity(currentLiana.getLinearVelocity());
			return; // TODO restore model transform back ?
		}
		
		physics.body.setTransform(currentLiana.getPosition(), currentLiana.getAngle());
		model.modelInstance.transform.setToTranslation(currentLiana.getPosition().x, currentLiana.getPosition().y, 0); 
		model.modelInstance.transform.rotate(0,0, 1, currentLiana.getAngle() * MathUtils.radiansToDegrees);
		// model.modelInstance.transform.rotate(0,1, 0, body.getLinearVelocity().x < 0 ? -90 : 90);
		
		float force = 0;
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
			force = -lianaForce;
		else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			force = lianaForce;
		
		currentLiana.applyForce(force, 0, physics.body.getPosition().x, physics.body.getPosition().y, true);
		
	}
	
	private void updateSwim()
	{
		Body body = physics.body;
		Vector2 vel = body.getLinearVelocity().cpy();
		float speed = swimForce;
		
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
		model.animationController.current.speed = 0.5f;
		if(vel.y != 0 || vel.x != 0) model.animationController.paused = false;
		
		// body.setLinearVelocity(vel);
		body.applyForceToCenter(vel.scl(0.5f), true);
		
		float angle = -90 + MathUtils.atan2(body.getLinearVelocity().y, body.getLinearVelocity().x) * MathUtils.radiansToDegrees;
		
		// TODO refactor with walk state ?
		if(model.animationController != null && model.animationController.current != null){
			model.modelInstance.transform.setToTranslation(physics.body.getPosition().x, physics.body.getPosition().y -0.2f, 0); // XXX hard coded offset from body ... 
			model.modelInstance.transform.rotate(0,0, 1, angle);
			model.modelInstance.transform.rotate(0,1, 0, body.getLinearVelocity().x < 0 ? -90 : 90);
			
		}
		// System.out.println(angle);
	}
	
	private void updateClimbing(float deltaTime)
	{
		Body body = physics.body;
		Vector2 vel = body.getLinearVelocity();
		float speed = climbSpeed;
		
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
