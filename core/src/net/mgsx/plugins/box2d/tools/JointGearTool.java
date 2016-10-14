package net.mgsx.plugins.box2d.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;

import net.mgsx.plugins.box2d.model.BodyItem;
import net.mgsx.plugins.box2d.model.WorldItem;

public class JointGearTool extends JointTool<GearJointDef> {
	public JointGearTool(Camera camera, WorldItem worldItem) {
		super("Gear", camera, worldItem, 2);
	}

	@Override
	protected GearJointDef createJoint(BodyItem bodyA, BodyItem bodyB) {

		GearJointDef def = new GearJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
//		def.localAnchorA.set(new Vector2(dots.get(0)).sub(bodyA.body
//				.getPosition()));
//		def.localAnchorB.set(new Vector2(dots.get(1)).sub(bodyB.body
//				.getPosition()));
		// TODO joint 1 and 2 ... ?
		return def;
	}
}
