package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.logic.LifeComponent;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("alive")
public class AliveCondition extends ConditionTask
{
	@Override
	public boolean match() {
		LifeComponent life = LifeComponent.components.get(getEntity());
		if(life != null && life.life < 0) return false;
		return true;
	}

}
