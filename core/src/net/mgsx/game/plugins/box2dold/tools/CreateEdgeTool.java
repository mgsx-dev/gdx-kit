package net.mgsx.game.plugins.box2dold.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.tools.MultiClickTool;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;
import net.mgsx.game.plugins.box2dold.commands.Box2DCommands;

public class CreateEdgeTool extends MultiClickTool
{
	private WorldItem worldItem;
	
	public CreateEdgeTool(Editor editor, WorldItem worldItem) {
		super("Edge", editor, 4);
		this.worldItem = worldItem;
	}


	@Override
	protected void complete() 
	{
		final Box2DBodyModel bodyItem = worldItem.currentBody("Chain Loop", dots.get(0).x, dots.get(0).y);
		
		// TODO not good ...
		EdgeShape shape = new EdgeShape();
		shape.set(bodyItem.body.getPosition(), new Vector2(dots.get(2)).sub(bodyItem.body.getPosition()));
		shape.setVertex0(new Vector2(dots.get(0)).sub(bodyItem.body.getPosition()));
		shape.setVertex3(new Vector2(dots.get(3)).sub(bodyItem.body.getPosition()));
		shape.setHasVertex0(true);
		shape.setHasVertex3(true);
		FixtureDef def = worldItem.settings.fixture();
		def.shape = shape;
		
		worldItem.performCommand(Box2DCommands.addShape(worldItem, bodyItem, def));
	}

}
