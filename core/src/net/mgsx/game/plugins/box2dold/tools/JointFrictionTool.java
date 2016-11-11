package net.mgsx.game.plugins.box2dold.tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;

import net.mgsx.game.core.Editor;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;

public class JointFrictionTool extends JointTool<FrictionJointDef> {
	public JointFrictionTool(Editor editor, WorldItem worldItem) {
		super("Friction", editor, worldItem, 2);
	}

	@Override
	protected FrictionJointDef createJoint(Box2DBodyModel bodyA, Box2DBodyModel bodyB) {

		FrictionJointDef def = new FrictionJointDef();
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
