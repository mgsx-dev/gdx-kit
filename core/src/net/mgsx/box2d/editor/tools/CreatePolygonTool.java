package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import net.mgsx.box2d.editor.commands.Box2DCommands;
import net.mgsx.box2d.editor.model.BodyItem;
import net.mgsx.box2d.editor.model.WorldItem;
import net.mgsx.fwk.editor.tools.MultiClickTool;

public class CreatePolygonTool extends MultiClickTool
{
	private WorldItem worldItem;
	
	public CreatePolygonTool(Camera camera, WorldItem worldItem) {
		super("Polygon", camera);
		this.worldItem = worldItem;
	}


	@Override
	protected void complete() 
	{
		if(dots.size < 3) return;
		
		BodyItem bodyItem = worldItem.currentBody("Polygon", dots.get(0).x, dots.get(0).y);
		
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
