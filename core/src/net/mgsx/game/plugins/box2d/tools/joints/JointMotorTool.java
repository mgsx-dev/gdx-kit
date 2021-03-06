package net.mgsx.game.plugins.box2d.tools.joints;

import com.badlogic.gdx.physics.box2d.joints.MotorJointDef;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;

public class JointMotorTool extends JointTool<MotorJointDef> {
	public JointMotorTool(EditorScreen editor, Box2DWorldContext worldItem) {
		super("Motor", editor, worldItem, 2);
	}

	@Override
	protected MotorJointDef createJoint(Box2DBodyModel bodyA, Box2DBodyModel bodyB) {

		MotorJointDef def = new MotorJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
		return def;
	}
}
