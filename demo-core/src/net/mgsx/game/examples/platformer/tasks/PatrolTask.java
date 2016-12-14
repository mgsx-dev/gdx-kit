package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.examples.platformer.ai.PatrolState;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;

public class PatrolTask extends EntityLeafTask
{
	@Override
	public void start() {
		// set patrol state
		Entity entity = getObject().entity;
		entity.add(getEngine().createComponent(PatrolState.class));
	}
	
	@Override
	public Status execute() 
	{
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		Entity entity = getObject().entity;
		entity.remove(PatrolState.class);
	}
}
