package net.mgsx.game.plugins.box2d.helper;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
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
}
