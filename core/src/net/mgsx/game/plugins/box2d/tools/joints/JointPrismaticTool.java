package net.mgsx.game.plugins.box2d.tools.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.systems.Box2DWorldContext;

public class JointPrismaticTool extends JointTool<PrismaticJointDef> {
	public JointPrismaticTool(EditorScreen editor, Box2DWorldContext worldItem) 
	{
		super("Prismatic", editor, worldItem, 2);
	}
	@Override
	protected PrismaticJointDef createJoint(Box2DBodyModel bodyA, Box2DBodyModel bodyB) {
		
		PrismaticJointDef def = new PrismaticJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
		def.localAnchorA.set(new Vector2(dots.get(0)).sub(bodyA.body.getPosition()));
		def.localAnchorB.set(new Vector2(dots.get(1)).sub(bodyB.body.getPosition()));
		
		return def;
	}
}
