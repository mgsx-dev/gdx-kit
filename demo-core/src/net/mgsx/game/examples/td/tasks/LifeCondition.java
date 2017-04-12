package net.mgsx.game.examples.td.tasks;

import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("life?")
public class LifeCondition extends EntityLeafTask
{
	@TaskAttribute
	public float max = 1;
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		Life life = Life.components.get(getEntity());
		if(life == null) return Status.SUCCEEDED;
		return life.current < max ? Status.SUCCEEDED : Status.FAILED;
	}
}
