package net.mgsx.game.plugins.box2d.tools.shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.MultiClickTool;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.box2d.tools.Box2DCommands;

public class CreateEdgeTool extends MultiClickTool
{
	private Box2DWorldContext worldItem;
	
	public CreateEdgeTool(EditorScreen editor, Box2DWorldContext worldItem) {
		super("Edge", editor, 4);
		this.worldItem = worldItem;
	}


	@Override
	protected void complete() 
	{
		final Box2DBodyModel bodyItem = worldItem.currentBody("Chain Loop", dots.get(0).x, dots.get(0).y);
		
		// TODO not good ...
		EdgeShape shape = new EdgeShape();
		shape.set(new Vector2(dots.get(1)).sub(bodyItem.body.getPosition()), new Vector2(dots.get(2)).sub(bodyItem.body.getPosition()));
		shape.setVertex0(new Vector2(dots.get(0)).sub(bodyItem.body.getPosition()));
		shape.setVertex3(new Vector2(dots.get(3)).sub(bodyItem.body.getPosition()));
		shape.setHasVertex0(true);
		shape.setHasVertex3(true);
		FixtureDef def = worldItem.settings.fixture();
		def.shape = shape;
		
		editor.performCommand(Box2DCommands.addShape(worldItem, bodyItem, def));
	}

}
