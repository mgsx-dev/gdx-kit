package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.states.WalkingState;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("walk")
public class WalkingTask extends EntityLeafTask
{
	@Override
	public void start() {
		getEntity().add(getEngine().createComponent(WalkingState.class));
	}
	@Override
	public Status execute() {
		return Status.RUNNING;
	}
	@Override
	public void end() {
		getEntity().remove(WalkingState.class);
	}
}
