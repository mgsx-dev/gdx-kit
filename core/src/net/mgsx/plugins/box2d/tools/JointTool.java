package net.mgsx.plugins.box2d.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.JointDef;

import net.mgsx.core.tools.MultiClickTool;
import net.mgsx.plugins.box2d.commands.Box2DCommands;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;

abstract public class JointTool<T extends JointDef> extends MultiClickTool 
{
	protected WorldItem worldItem;
	
	public JointTool(String name, Camera camera, WorldItem worldItem, int maxPoints) 
	{
		super("Create " + name, camera, maxPoints);
		this.worldItem = worldItem;
	}
	
	protected abstract T createJoint(BodyItem bodyA, BodyItem bodyB);
	
	@Override
	protected void complete() 
	{
		if(worldItem.selection.bodies.size < 2){
			return;
		}
		BodyItem bodyA = worldItem.selection.bodies.get(worldItem.selection.bodies.size-2);
		BodyItem bodyB = worldItem.selection.bodies.get(worldItem.selection.bodies.size-1);
		
		final T def = createJoint(bodyA, bodyB);
		
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		
		worldItem.performCommand(Box2DCommands.addJoint(worldItem, name, def));
	}
}
