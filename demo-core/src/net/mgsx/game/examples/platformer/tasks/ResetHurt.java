package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.inputs.PlayerController;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("resetHurt")
public class ResetHurt extends EntityLeafTask{

	@Override
	public Status execute() {
		PlayerController player = PlayerController.components.get(getEntity());
		if(player != null){
			player.isHurt = false;
		}
		return Status.SUCCEEDED;
	}
}
