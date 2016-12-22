package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("onGround")
public class OnGroundCondition extends ConditionTask
{

	@Override
	public boolean match() {
		PlayerController player = PlayerController.components.get(getEntity());
		if(player == null) return false;
		return player.onGround;
	}

}
