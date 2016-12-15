package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.examples.platformer.logic.PlayerSensor;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.EntityBlackboard;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("farPlayer")
public class FarPlayer extends EntityLeafTask
{
	@TaskAttribute
	public float distance;
	
	@Override
	public Status execute() 
	{
		PlayerSensor sensor = PlayerSensor.components.get(getObject().entity);
		if(sensor == null){
			getObject().entity.add(getEngine().createComponent(PlayerSensor.class));
		}else{
			if(sensor.exists && sensor.distance < distance)
				return Status.FAILED;
		}
		return Status.SUCCEEDED;
	}
	
	@Override
	protected Task<EntityBlackboard> copyTo(Task<EntityBlackboard> task) {
		FarPlayer clone = (FarPlayer)task;
		clone.distance = distance;
		return super.copyTo(task);
	}
}
