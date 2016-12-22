package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("alive")
public class AliveCondition extends ConditionTask
{

	@Override
	public boolean match() {
		return true; // LifeComponent.components.has(getEntity());
	}

}
