package net.mgsx.game.plugins.box2dold.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.tools.MultiClickTool;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.box2dold.commands.Box2DCommands;
import net.mgsx.game.plugins.box2dold.model.WorldItem;

public class CreatePolygonTool extends MultiClickTool
{
	private WorldItem worldItem;
	
	public CreatePolygonTool(Editor editor, WorldItem worldItem) {
		super("Polygon", editor);
		this.worldItem = worldItem;
	}


	@Override
	protected void complete() 
	{
		if(dots.size < 3) return;
		
		Box2DBodyModel bodyItem = worldItem.currentBody("Polygon", dots.get(0).x, dots.get(0).y);
		
		PolygonShape shape = new PolygonShape();
		
		// TODO find another way or create helper
		Vector2 [] array = new Vector2[dots.size];
		for(int i=0 ; i<dots.size; i++) array[i] = new Vector2(dots.get(i)).sub(bodyItem.body.getPosition());
		shape.set(array);
		
		FixtureDef def = worldItem.settings.fixture();
		def.shape = shape;
		
		worldItem.performCommand(Box2DCommands.addShape(worldItem, bodyItem, def));
	}

}