package net.mgsx.game.examples.td.tasks;

import net.mgsx.game.examples.td.components.MultiTarget;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("target?")
public class HasTargetCondition extends EntityLeafTask
{
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		SingleTarget singleTarget = SingleTarget.components.get(getEntity());
		if(singleTarget != null && singleTarget.target != null)
			return Status.SUCCEEDED;
		MultiTarget multiTarget = MultiTarget.components.get(getEntity());
		if(multiTarget != null && multiTarget.targets.size > 0)
			return Status.SUCCEEDED;
		return Status.FAILED;
	}
}
