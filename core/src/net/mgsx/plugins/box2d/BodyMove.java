package net.mgsx.plugins.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import net.mgsx.core.plugins.Movable;

public class BodyMove  extends Movable
{
	private Body body;
	private Vector2 position = new Vector2();
	
	public BodyMove(Body body) {
		this.body = body;
	}
	
	@Override
	public void moveBegin(Entity entity) {
		body.setGravityScale(0);
	}

	@Override
	public void moveEnd(Entity entity) {
		body.setGravityScale(1);
		body.applyForceToCenter(0, 0, true);
	}
	
	@Override
	public void move(Entity entity, Vector3 deltaWorld) 
	{
		position.set(body.getPosition());
		position.add(deltaWorld.x, deltaWorld.y);
		
		body.setAngularVelocity(0);
		body.setGravityScale(0);
		body.setLinearVelocity(0, 0);
		body.setTransform(position, body.getAngle());
		body.applyForceToCenter(0, 0, true); // wakeup to allow collisions !
	}
	
	@Override
	public void moveTo(Entity entity, Vector3 pos) 
	{
		position.set(pos.x, pos.y);
		body.setTransform(position, body.getAngle());
	}
	
	@Override
	public void getPosition(Entity entity, Vector3 pos) 
	{
		pos.set(body.getPosition().x, body.getPosition().y, 0);
	}


}