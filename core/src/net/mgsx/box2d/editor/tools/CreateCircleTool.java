package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Shape;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.FixtureItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.Tool;
import net.mgsx.fwk.editor.tools.RectangleTool;

public class CreateCircleTool extends RectangleTool {

	private WorldItem worldItem;
	private BodyItem bodyItem;
	
	
	public CreateCircleTool(Camera camera, WorldItem worldItem, BodyItem bodyItem) {
		super("Circle", camera);
		this.worldItem = worldItem;
		this.bodyItem = bodyItem;
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		float r = startPoint.dst(endPoint);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(r);

		FixtureDef fix = worldItem.settings.fixture();
		fix.shape = shape;
		
		BodyDef def = worldItem.settings.body();
		def.position.set(startPoint);
		
		Body body = worldItem.world.createBody(def);
		BodyItem bodyItem = new BodyItem("Circle", def, body);
		bodyItem.fixtures.add(new FixtureItem("Chain", fix, bodyItem.body.createFixture(fix)));
		worldItem.items.bodies.add(bodyItem);
	}
	
	@Override
	public void render(ShapeRenderer renderer) {
		if(startPoint != null && endPoint != null){
			renderer.begin(ShapeType.Line);
			renderer.line(startPoint.x,startPoint.y,endPoint.x, endPoint.y);
			renderer.end();
		}
	}
	
	
}
