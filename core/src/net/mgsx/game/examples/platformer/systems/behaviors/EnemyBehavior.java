package net.mgsx.game.examples.platformer.systems.behaviors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.core.components.LogicBehavior;
import net.mgsx.game.core.plugins.Initializable;
import net.mgsx.game.examples.platformer.components.EnemyComponent;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class EnemyBehavior implements LogicBehavior, Initializable
{
	private Box2DBodyModel body;
	
	private int direction = 0;
	
	private Entity entity;
	private EnemyComponent enemy;
	private Engine manager;
	@Override
	public void initialize(Engine manager, Entity entity) {
		this.manager = manager;
		this.entity = entity;
		
		body = entity.getComponent(Box2DBodyModel.class);
		enemy = entity.getComponent(EnemyComponent.class);
		// box = entity.getComponent(BoundaryComponent.class).box;
	}
	
	@Override
	public void update(float deltaTime) 
	{
		if(!enemy.alive){
			if(body.body.isActive()){
				// TODO hide model (set visibility component to hidden or model.hide = true)
				BoundingBox box = entity.getComponent(BoundaryComponent.class).box;
				body.body.setTransform(
						box.getCenterX(),
						box.getCenterY()
						, 0);
				body.body.setActive(false);
				
				entity.add(manager.createComponent(Hidden.class));
			}
			return;
		}
		// System.out.println(outsideCounter);
		// TODO query box2D 
		// TODO 
		// body.context.queryFirstBody(pos);
//		if(isOutside){
//			
//			if( body.body.getLinearVelocity().x * direction > 0) direction = body.body.getLinearVelocity().x > 0 ? 1 : -1;
//			// direction = -direction;
//			// body.body.setLinearVelocity(-direction * 0.5f, 0);
//			// body.body.applyTorque(0.1f * direction, true);
//			isOutside = false;
//		}
		
		// if(body.body.getPosition().sub(startup).len() > 10 || body.body.getLinearVelocity().x < 0.1f) direction = body.body.getPosition().sub(startup).x > 0 ? -1 : 1;

		if(Math.abs(body.body.getLinearVelocity().len()) < 0.001f) direction = body.body.getLinearVelocity().x > 0 ? -1 : 1;

		
		//		body.fixtures.get(0).fixture.setFriction(0.5f);
//		body.fixtures.get(0).fixture.setRestitution(0.3f);
//		body.body.setAngularVelocity(8f * direction);
		// body.body.setLinearVelocity(1f * direction, 0);
		if(Math.abs(body.body.getLinearVelocity().x) < 1f)
			body.body.applyForceToCenter(5.5f * direction, 0, true);
		
		Entity entity = (Entity)body.body.getUserData();
		
		Transform2DComponent tr = entity.getComponent(Transform2DComponent.class);
		if(tr != null){
			tr.enabled = false;
		}
		
		G3DModel model = entity.getComponent(G3DModel.class);
		if(model != null){
			
			// XXX patch animation current
			model.animationController.allowSameAnimation = true;
			if(model.animationController.current == null) 
				model.animationController.setAnimation(model.modelInstance.animations.first().id, -1);
			
			model.modelInstance.transform.idt();
			model.modelInstance.transform.translate(body.body.getPosition().x, body.body.getPosition().y - 0.1f, 0);
			model.modelInstance.transform.rotate(0, 0, 1, body.body.getAngle() * MathUtils.radiansToDegrees);
			model.modelInstance.transform.rotate(0, 1, 0, body.body.getLinearVelocity().x > 0 ? 90 : -90);
		}
	}

	@Override
	public void enter() {
		if(enemy.alive) return;
		body.body.setActive(true);
		
		entity.remove(Hidden.class);
		enemy.alive = true;
		// XXX place it from boundary
		BoundingBox box = entity.getComponent(BoundaryComponent.class).box;
		body.body.setTransform(
				box.getCenterX(),
				box.getCenterY()
				, 0);
		
		direction = 0;
	}

	@Override
	public void exit() {
		// nothing on exit !
		// body.body.setActive(false);
		// TODO stop animations as well ?
	}



}
