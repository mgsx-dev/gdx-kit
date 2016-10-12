package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.FixtureItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.tools.RectangleTool;

public class CreateRectangleTool extends RectangleTool {

	private WorldItem worldItem;
	
	public CreateRectangleTool(Camera camera, WorldItem worldItem) {
		super("Rectangle", camera);
		this.worldItem = worldItem;
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		float x = (startPoint.x + endPoint.x) / 2;
		float y = (startPoint.y + endPoint.y) / 2;
		float w = Math.abs(startPoint.x - endPoint.x);
		float h = Math.abs(startPoint.y - endPoint.y);

		BodyItem bodyItem = worldItem.currentBody("Rectangle", x, y);
		
		PolygonShape pshape = new PolygonShape();
		pshape.setAsBox(w/2, h/2, new Vector2(x, y).sub(bodyItem.body.getPosition()), 0); 
		
		FixtureDef fix = worldItem.settings.fixture();
		fix.shape = pshape;
		
		bodyItem.fixtures.add(new FixtureItem("Polygon", fix, bodyItem.body.createFixture(fix)));
	}
	
	
}
