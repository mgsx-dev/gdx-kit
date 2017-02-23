package net.mgsx.game.plugins.box2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import net.mgsx.game.core.components.Movable;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;

public class BodyMove  extends Movable
{
	private Body body;
	private Vector2 position = new Vector2();
	
	public BodyMove(Body body) {
		this.body = body;
	}
	
	@Override
	public void moveBegin(Entity entity) {
		// body.setGravityScale(0);
	}

	@Override
	public void moveEnd(Entity entity) {
		// body.setGravityScale(1);
		body.setLinearVelocity(0, 0);
		body.applyForceToCenter(0, 0, true);
		updatePosition(entity, body.getPosition());
	}
	
	@Override
	public void move(Entity entity, Vector3 deltaWorld) 
	{
		position.set(body.getPosition());
		position.add(deltaWorld.x, deltaWorld.y);
		
		body.setAngularVelocity(0);
		// body.setGravityScale(0);
		body.setLinearVelocity(0, 0);
		
		body.setLinearVelocity(deltaWorld.x, deltaWorld.y); // ensure to wake others !
		body.setTransform(position, body.getAngle());
		body.applyForceToCenter(0, 0, true); // wakeup to allow collisions !
		updatePosition(entity, position);
	}
	
	private void updatePosition(Entity entity, Vector2 position)
	{
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		if(physics != null){
			physics.def.position.set(position);
		}
	}
	
	@Override
	public void moveTo(Entity entity, Vector3 pos) 
	{
		position.set(pos.x, pos.y);
		body.setTransform(position, body.getAngle());
		updatePosition(entity, position);
	}
	
	@Override
	public void getPosition(Entity entity, Vector3 pos) 
	{
		pos.set(body.getPosition().x, body.getPosition().y, 0);
	}
	
	@Override
	public float getRotation(Entity entity) {
		return body.getAngle() * MathUtils.radiansToDegrees;
	}


}