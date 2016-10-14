package net.mgsx.plugins.box2d.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class JointWeldTool extends JointTool<WeldJointDef> {
	public JointWeldTool(Camera camera, WorldItem worldItem) {
		super("Weld", camera, worldItem, 2);
	}

	@Override
	protected WeldJointDef createJoint(BodyItem bodyA, BodyItem bodyB) {

		WeldJointDef def = new WeldJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
		def.localAnchorA.set(new Vector2(dots.get(0)).sub(bodyA.body
				.getPosition()));
		def.localAnchorB.set(new Vector2(dots.get(1)).sub(bodyB.body
				.getPosition()));

		return def;
	}
}
