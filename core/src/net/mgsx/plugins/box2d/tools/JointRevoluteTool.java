package net.mgsx.plugins.box2d.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import net.mgsx.core.Editor;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class JointRevoluteTool extends JointTool<RevoluteJointDef> {
	public JointRevoluteTool(Editor editor, WorldItem worldItem) 
	{
		super("Revolute", editor, worldItem, 2);
	}
	@Override
	protected RevoluteJointDef createJoint(BodyItem bodyA, BodyItem bodyB) {
		
		RevoluteJointDef def = new RevoluteJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
		def.localAnchorA.set(new Vector2(dots.get(0)).sub(bodyA.body.getPosition()));
		def.localAnchorB.set(new Vector2(dots.get(1)).sub(bodyB.body.getPosition()));
		
		return def;
	}
}
