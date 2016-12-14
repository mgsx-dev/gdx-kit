package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;

public class IdleTask extends EntityLeafTask
{

	@Override
	public Status execute() {
		return Status.SUCCEEDED;
	}
}
