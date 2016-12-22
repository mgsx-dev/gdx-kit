package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;

abstract public class ConditionTask extends EntityLeafTask {

	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		return match() ? Status.SUCCEEDED : Status.FAILED;
	}
	
	abstract public boolean match();
}
