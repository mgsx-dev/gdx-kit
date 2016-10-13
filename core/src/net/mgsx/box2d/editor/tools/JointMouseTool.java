package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

import net.mgsx.box2d.editor.model.BodyItem;
import net.mgsx.box2d.editor.model.WorldItem;

public class JointMouseTool extends JointTool<MouseJointDef> {
	public JointMouseTool(Camera camera, WorldItem worldItem) {
		super("Mouse", camera, worldItem, 2);
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
