package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.WorldItem;

public class JointPrismaticTool extends JointTool<PrismaticJointDef> {
	public JointPrismaticTool(Camera camera, WorldItem worldItem) 
	{
		super("Prismatic", camera, worldItem, 2);
	}
	@Override
	protected PrismaticJointDef createJoint(BodyItem bodyA, BodyItem bodyB) {
		
		PrismaticJointDef def = new PrismaticJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
		def.localAnchorA.set(new Vector2(dots.get(0)).sub(bodyA.body.getPosition()));
		def.localAnchorB.set(new Vector2(dots.get(1)).sub(bodyB.body.getPosition()));
		
		return def;
	}
}
