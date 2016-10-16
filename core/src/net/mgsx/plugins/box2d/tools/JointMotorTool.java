package net.mgsx.plugins.box2d.tools;

import com.badlogic.gdx.physics.box2d.joints.MotorJointDef;

import net.mgsx.core.Editor;
import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class JointMotorTool extends JointTool<MotorJointDef> {
	public JointMotorTool(Editor editor, WorldItem worldItem) {
		super("Motor", editor, worldItem, 2);
	}

	@Override
	protected MotorJointDef createJoint(BodyItem bodyA, BodyItem bodyB) {

		MotorJointDef def = new MotorJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
		return def;
	}
}
