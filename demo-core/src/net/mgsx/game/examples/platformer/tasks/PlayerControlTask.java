package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("control")
public class PlayerControlTask extends ComponentTask
{
	public PlayerControlTask() {
		super(PlayerController.class);
	}

}
