package net.mgsx.game.plugins.box2d.tools.shapes;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;
import net.mgsx.game.plugins.box2dold.commands.Box2DCommands;

public class CreateCircleTool extends RectangleTool {

	private WorldItem worldItem;
	
	
	public CreateCircleTool(EditorScreen editor, WorldItem worldItem) {
		super("Circle", editor);
		this.worldItem = worldItem;
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		final Box2DBodyModel bodyItem = worldItem.currentBody("Circle", startPoint.x, startPoint.y);

		float r = startPoint.dst(endPoint);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(r);
		shape.setPosition(new Vector2(startPoint).sub(bodyItem.body.getPosition()));

		FixtureDef fix = worldItem.settings.fixture();
		fix.shape = shape;
		
		worldItem.performCommand(Box2DCommands.addShape(worldItem, bodyItem, fix));
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