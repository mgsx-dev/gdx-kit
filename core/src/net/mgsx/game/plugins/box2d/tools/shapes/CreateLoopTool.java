package net.mgsx.game.plugins.box2d.tools.shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.box2d.tools.Box2DCommands;

@Editable
public class CreateLoopTool extends AbstractDotShapeTool
{
	public CreateLoopTool(EditorScreen editor, Box2DWorldContext worldItem) {
		super("Loop", editor, worldItem);
	}


	@Override
	protected void complete() 
	{
		if(dots.size < 3) return;
		
		final Box2DBodyModel bodyItem = worldItem.currentBody("Chain Loop", dots.get(0).x, dots.get(0).y);
		
		ChainShape shape = new ChainShape();
		// TODO find another way or create helper
		Vector2 [] array = new Vector2[dots.size];
		for(int i=0 ; i<dots.size; i++) array[i] = new Vector2(dots.get(i)).sub(bodyItem.body.getPosition());
		shape.createLoop(array);
		
		FixtureDef def = worldItem.settings.fixture();
		def.shape = shape;
		
		historySystem.performCommand(Box2DCommands.addShape(worldItem, bodyItem, def));
	}

}
