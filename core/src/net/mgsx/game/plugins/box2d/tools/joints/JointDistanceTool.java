package net.mgsx.game.plugins.box2d.tools.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.box2d.components.WorldItem;

public class JointDistanceTool extends JointTool<DistanceJointDef> {
	public JointDistanceTool(EditorScreen editor, WorldItem worldItem) {
		super("Distance", editor, worldItem, 2);
	}

	@Override
	protected DistanceJointDef createJoint(Box2DBodyModel bodyA, Box2DBodyModel bodyB) {

		DistanceJointDef def = new DistanceJointDef();
		def.bodyA = bodyA.body;
		def.bodyB = bodyB.body;
		def.collideConnected = true;
		def.length = dots.get(0).dst(dots.get(1));
		
		def.localAnchorA.set(new Vector2(dots.get(0)).sub(bodyA.body
				.getPosition()));
		def.localAnchorB.set(new Vector2(dots.get(1)).sub(bodyB.body
				.getPosition()));

		return def;
	}
}
