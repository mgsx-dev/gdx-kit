package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.Tool;
import net.mgsx.fwk.editor.tools.RectangleTool;

public class CreateRectangleTool extends RectangleTool {

	private WorldItem worldItem;
	private BodyItem bodyItem;
	
	
	public CreateRectangleTool(Camera camera, WorldItem worldItem, BodyItem bodyItem) {
		super("Rectangle", camera);
		this.worldItem = worldItem;
		this.bodyItem = bodyItem;
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		float x = (startPoint.x + endPoint.x) / 2;
		float y = (startPoint.y + endPoint.y) / 2;
		float w = Math.abs(startPoint.x - endPoint.x);
		float h = Math.abs(startPoint.y - endPoint.y);
		
		PolygonShape pshape = new PolygonShape();
		pshape.setAsBox(w/2, h/2); 
		
		BodyDef def = worldItem.settings.body();
		def.position.set(x, y);
		
		// TODO fixture template from worldItem (from user edit)
		FixtureDef fix = worldItem.settings.fixture();
		fix.shape = pshape;
		
		Body body = worldItem.world.createBody(def);
		body.createFixture(fix);

		worldItem.items.bodies.add(new BodyItem("Ball", def, body));
	}
	
	
}
