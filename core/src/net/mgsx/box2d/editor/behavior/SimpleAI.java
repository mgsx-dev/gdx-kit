package net.mgsx.box2d.editor.behavior;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Fixture;

public class SimpleAI extends BodyBehavior
{
	private boolean jumping = false;
	private boolean toTheRight = false;
	
	private Vector2 groundSensorStart = new Vector2();
	private Vector2 groundSensorEnd = new Vector2();
	private Vector2 wallSensorStart = new Vector2();
	private Vector2 wallSensorEnd = new Vector2();
	
	@Override
	public void act() 
	{
		if(jumping){
			groundSensorStart.set(bodyItem.body.getPosition());
			groundSensorEnd.set(groundSensorStart).add(0,-0.5f);
			Fixture fixture = worldItem.rayCastFirst(groundSensorStart, groundSensorEnd);
			if(fixture != null && fixture.getBody() != bodyItem.body){
				jumping = false;
			}
		}else{
			// ray cast at direction, if ground then ok move forward else wait and go back
			groundSensorStart.set(bodyItem.body.getPosition());
			groundSensorEnd.set(groundSensorStart).add(0,-0.5f);
			Fixture fixtureGround = worldItem.rayCastFirst(groundSensorStart, groundSensorEnd);
			
			wallSensorStart.set(bodyItem.body.getPosition());
			wallSensorEnd.set(groundSensorStart).add(toTheRight ? 0.5f : -0.5f, 0);
			
			Fixture fixtureWall = worldItem.rayCastFirst(wallSensorStart, wallSensorEnd);
			if(fixtureGround != null && fixtureWall == null){
				// jump
				bodyItem.body.setLinearVelocity(toTheRight ? 1f : -1f, 4f);
				jumping = true;
			}else{
				toTheRight = !toTheRight;
			}
		}
	}
	
	@Override
	public void renderDebug(ShapeRenderer renderer) {
		renderer.end();
		renderer.begin(ShapeType.Line);
		renderer.line(groundSensorStart, groundSensorEnd);
		renderer.line(wallSensorStart, wallSensorEnd);
		renderer.end();
		renderer.begin(ShapeType.Line);
	}

}
