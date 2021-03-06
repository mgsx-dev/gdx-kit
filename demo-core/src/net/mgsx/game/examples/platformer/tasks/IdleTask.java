package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("idle")
public class IdleTask extends ConditionTask
{

	@Override
	public boolean match() {
		PlayerController player = PlayerController.components.get(getEntity());
		if(player == null) return false;
		Box2DBodyModel physics = Box2DBodyModel.components.get(getEntity());
		if(physics == null) return false;
		return player.onGround && physics.body.getLinearVelocity().len() < 0.1f;
	}
}
