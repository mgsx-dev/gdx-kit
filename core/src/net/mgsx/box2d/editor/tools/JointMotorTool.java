package net.mgsx.box2d.editor.tools;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.WorldItem;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.MotorJointDef;

public class JointMotorTool extends JointTool<MotorJointDef> {
	public JointMotorTool(Camera camera, WorldItem worldItem) {
		super("Motor", camera, worldItem, 2);
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
