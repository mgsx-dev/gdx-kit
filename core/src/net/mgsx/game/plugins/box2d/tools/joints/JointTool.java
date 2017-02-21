package net.mgsx.game.plugins.box2d.tools.joints;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.MultiClickTool;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;
import net.mgsx.game.plugins.box2d.tools.Box2DCommands;

abstract public class JointTool<T extends JointDef> extends MultiClickTool 
{
	protected Box2DWorldContext worldItem;
	
	public JointTool(String name, EditorScreen editor, Box2DWorldContext worldItem, int maxPoints) 
	{
		super("Create " + name, editor, maxPoints);
		this.worldItem = worldItem;
	}
	
	@Override
	public boolean allowed(Array<Entity> selection) 
	{
		// allow only 2 bodies (maybe gears will override this rule : only 2 joints ?)
		if(selection.size != 2) return false;
		return Box2DBodyModel.components.has(selection.first()) &&
				Box2DBodyModel.components.has(selection.peek());
	}
	
	protected abstract T createJoint(Box2DBodyModel bodyA, Box2DBodyModel bodyB);
	
	@Override
	protected void complete() 
	{
		// TODO based on editor !
		if(selection().size() < 2){
			return;
		}
		Box2DBodyModel bodyA = selection().last(1).getComponent(Box2DBodyModel.class);
		Box2DBodyModel bodyB = selection().last().getComponent(Box2DBodyModel.class);
		
		final T def = createJoint(bodyA, bodyB);
		
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		
		editor.performCommand(Box2DCommands.addJoint(editor, worldItem.world, name, def));
		
		end();
	}
}
