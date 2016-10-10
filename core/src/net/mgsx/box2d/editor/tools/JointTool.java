package net.mgsx.box2d.editor.tools;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.Box2DPresets.JointItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.tools.MultiClickTool;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;

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
		
		T def = createJoint(bodyA, bodyB);
		
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		
		Joint joint = worldItem.world.createJoint(def);
		
		worldItem.items.joints.add(new JointItem(name, def, joint));
	}
}
