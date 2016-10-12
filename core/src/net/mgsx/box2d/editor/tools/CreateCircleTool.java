package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.FixtureItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.tools.RectangleTool;

public class CreateCircleTool extends RectangleTool {

	private WorldItem worldItem;
	
	
	public CreateCircleTool(Camera camera, WorldItem worldItem) {
		super("Circle", camera);
		this.worldItem = worldItem;
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		final BodyItem bodyItem = worldItem.currentBody("Circle", startPoint.x, startPoint.y);

		float r = startPoint.dst(endPoint);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(r);
		shape.setPosition(new Vector2(startPoint).sub(bodyItem.body.getPosition()));

		FixtureDef fix = worldItem.settings.fixture();
		fix.shape = shape;
		
		bodyItem.fixtures.add(new FixtureItem("Circle", fix, bodyItem.body.createFixture(fix)));
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
