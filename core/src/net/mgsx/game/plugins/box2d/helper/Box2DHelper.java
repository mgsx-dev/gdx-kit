package net.mgsx.game.plugins.box2d.helper;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;

public class Box2DHelper {

	private static Vector2 position = new Vector2();
	
	public static void computeBoundary(Rectangle bounds, Body body){
		bounds.set(0, 0, 0, 0);
		for(Fixture fixture : body.getFixtureList()){
			if (fixture.getType() == Type.Circle) {
				CircleShape shape = (CircleShape)fixture.getShape();
				float radius = shape.getRadius();
				Vector2 p = shape.getPosition();
				bounds.merge(-p.x-radius, p.y-radius);
				bounds.merge(-p.x+radius, p.y+radius);
				// bounds.merge(rect.setSize(radius * 2).setCenter(shape.getPosition()));
			} else if (fixture.getType() == Type.Polygon) {
				PolygonShape shape = (PolygonShape)fixture.getShape();
				int vertexCount = shape.getVertexCount();
				for (int i = 0; i < vertexCount; i++) {
					shape.getVertex(i, position);
					bounds.merge(position);
				}
			}
		}
	}

	/**
	 * Clone body def
	 * @param dst def to copy to
	 * @param src def to copy from
	 * @return dst for chaining
	 */
	public static BodyDef clone(BodyDef dst, BodyDef src) 
	{
		dst.active = src.active;
		dst.allowSleep = src.allowSleep;
		dst.awake = src.awake;
		dst.bullet = src.bullet;
		dst.fixedRotation = src.fixedRotation;
		dst.angle = src.angle;
		dst.angularDamping = src.angularDamping;
		dst.angularVelocity = src.angularVelocity;
		dst.gravityScale = src.gravityScale;
		dst.linearDamping = src.linearDamping;
		dst.linearVelocity.set(src.linearVelocity);
		dst.position.set(src.position);
		dst.type = src.type;
		return dst;
	}
	
	/**
	 * Clone fixture def. 
	 * Note that shape is not cloned but copied by reference because shapes can't be directly modified,
	 * they are fully recreated so multiple fixture def referencing the same shape is not an issue.
	 * @param dst def to copy to
	 * @param src def to copy from
	 * @return dst for chaining
	 */
	public static FixtureDef clone(FixtureDef dst, FixtureDef src) 
	{
		dst.density = src.density;
		dst.filter.categoryBits = src.filter.categoryBits;
		dst.filter.groupIndex = src.filter.groupIndex;
		dst.filter.maskBits = src.filter.maskBits;
		dst.friction = src.friction;
		dst.isSensor = src.isSensor;
		dst.restitution = src.restitution;
		dst.shape = src.shape;
		return dst;
	}

}
