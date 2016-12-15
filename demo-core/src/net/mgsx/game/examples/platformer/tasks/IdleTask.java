package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
@TaskAlias("idle")
public class IdleTask extends EntityLeafTask
{

	@Override
	public Status execute() {
		return Status.SUCCEEDED;
	}
}
