package net.mgsx.game.plugins.box2d.tools.shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.box2d.tools.Box2DCommands;

// TODO java: /var/lib/jenkins/workspace/libgdx/extensions/gdx-box2d/gdx-box2d/jni/Box2D/Collision/Shapes/b2PolygonShape.cpp:430: virtual void b2PolygonShape::ComputeMass(b2MassData*, float32) const: Assertion `area > 1.19209289550781250000e-7F' failed.

@Editable
public class CreateRectangleTool extends AbstractBoundShapeTool {

	public CreateRectangleTool(EditorScreen editor, Box2DWorldContext worldItem) {
		super("Rectangle", editor, worldItem);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) 
	{
		float x = (startPoint.x + endPoint.x) / 2;
		float y = (startPoint.y + endPoint.y) / 2;
		float w = Math.abs(startPoint.x - endPoint.x);
		float h = Math.abs(startPoint.y - endPoint.y);

		Box2DBodyModel bodyItem = worldItem.currentBody("Rectangle", x, y);
		
		PolygonShape pshape = new PolygonShape();
		pshape.setAsBox(w/2, h/2, new Vector2(x, y).sub(bodyItem.body.getPosition()), 0); 
		
		FixtureDef fix = worldItem.settings.fixture();
		fix.shape = pshape;
		
		historySystem.performCommand(Box2DCommands.addShape(worldItem, bodyItem, fix));
	}
	
	
}
