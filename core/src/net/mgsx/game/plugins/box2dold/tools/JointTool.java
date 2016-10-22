package net.mgsx.game.plugins.box2dold.tools;

import com.badlogic.gdx.physics.box2d.JointDef;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.tools.MultiClickTool;
import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.box2dold.commands.Box2DCommands;
import net.mgsx.game.plugins.box2dold.model.WorldItem;

abstract public class JointTool<T extends JointDef> extends MultiClickTool 
{
	protected WorldItem worldItem;
	
	public JointTool(String name, Editor editor, WorldItem worldItem, int maxPoints) 
	{
		super("Create " + name, editor, maxPoints);
		this.worldItem = worldItem;
	}
	
	protected abstract T createJoint(Box2DBodyModel bodyA, Box2DBodyModel bodyB);
	
	@Override
	protected void complete() 
	{
		// TODO based on editor !
		if(worldItem.selection.bodies.size < 2){
			return;
		}
		Box2DBodyModel bodyA = worldItem.selection.bodies.get(worldItem.selection.bodies.size-2);
		Box2DBodyModel bodyB = worldItem.selection.bodies.get(worldItem.selection.bodies.size-1);
		
		final T def = createJoint(bodyA, bodyB);
		
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		
		worldItem.performCommand(Box2DCommands.addJoint(worldItem, name, def));
	}
}