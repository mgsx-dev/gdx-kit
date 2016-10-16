package net.mgsx.plugins.box2d.tools;

import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

import net.mgsx.core.Editor;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class JointMouseTool extends JointTool<MouseJointDef> {
	public JointMouseTool(Editor editor, WorldItem worldItem) {
		super("Mouse", editor, worldItem, 2);
	}

	@Override
	protected MouseJointDef createJoint(BodyItem bodyA, BodyItem bodyB) {

		MouseJointDef def = new MouseJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
		return def;
	}
}
