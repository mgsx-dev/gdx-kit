package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.examples.platformer.ai.PatrolComponent;
import net.mgsx.game.examples.platformer.ai.PatrolState;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
@TaskAlias("patrol")
public class PatrolTask extends EntityLeafTask
{
	@TaskAttribute
	public float speed = 1f;
	
	@Override
	public void start() {
		// set patrol state
		Entity entity = getObject().entity;
		entity.add(getEngine().createComponent(PatrolState.class));
		PatrolComponent.components.get(entity).speed = speed;
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
