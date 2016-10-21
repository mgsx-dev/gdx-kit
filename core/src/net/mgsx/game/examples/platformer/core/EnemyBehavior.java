package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

import net.mgsx.game.core.plugins.Initializable;
import net.mgsx.game.plugins.box2d.Box2DListener;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.G3DModel;

public class EnemyBehavior implements LogicBehavior, Initializable
{
	private Box2DBodyModel body;
	private int outsideCounter = 0;
	
	private int direction = 0;
	
	private Vector2 startup = new Vector2();
	
	
	@Override
	public void initialize(Engine manager, Entity entity) {
		body = entity.getComponent(Box2DBodyModel.class);
		startup.set(body.body.getPosition());
		body.fixtures.get(0).fixture.setUserData(new Box2DListener() {
			
			@Override
			public void endContact(Contact contact, Fixture self, Fixture other) {
				Entity otherEntity = (Entity)other.getBody().getUserData();
				if(otherEntity.getComponent(EnemyZone.class) != null){
					outsideCounter++;
					// direction = contact.getWorldManifold().getNormal().x > 0 ? 1 : -1;
				}
			}
			
			@Override
			public void beginContact(Contact contact, Fixture self, Fixture other) {
				Entity otherEntity = (Entity)other.getBody().getUserData();
				if(otherEntity.getComponent(EnemyZone.class) != null){
					outsideCounter--;
					
				}
			}
		});
	}
	
	@Override
	public void update(float deltaTime) 
	{
		boolean isOutside = outsideCounter > 0;
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
		if(body.body.getPosition().sub(startup).len() > 2) direction = body.body.getPosition().sub(startup).x > 0 ? -1 : 1;
//		body.fixtures.get(0).fixture.setFriction(0.5f);
//		body.fixtures.get(0).fixture.setRestitution(0.3f);
//		body.body.setAngularVelocity(8f * direction);
		// body.body.setLinearVelocity(1f * direction, 0);
		if(Math.abs(body.body.getLinearVelocity().x) < 1f)
			body.body.applyForceToCenter(2.5f * direction, 0, true);
		
		
		Entity entity = (Entity)body.body.getUserData();
		G3DModel model = entity.getComponent(G3DModel.class);
		if(model != null){
			model.modelInstance.transform.idt();
			model.modelInstance.transform.translate(body.body.getPosition().x, body.body.getPosition().y - 0.1f, 0);
			model.modelInstance.transform.rotate(0, 0, 1, body.body.getAngle() * MathUtils.radiansToDegrees);
			model.modelInstance.transform.rotate(0, 1, 0, body.body.getLinearVelocity().x > 0 ? 90 : -90);
		}
	}



}
