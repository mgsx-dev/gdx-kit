package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("isHurt")
public class PlayerIsHurt extends ConditionTask
{

	@Override
	public boolean match() 
	{
		PlayerController player = PlayerController.components.get(getEntity());
		return player.isHurt;
	}
}
