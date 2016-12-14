package net.mgsx.game.examples.platformer.tasks;

import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

import net.mgsx.game.examples.platformer.logic.PlayerSensor;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.EntityBlackboard;

public class NearPlayer extends EntityLeafTask
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
				return Status.SUCCEEDED;
		}
		return Status.FAILED;
	}
	
	@Override
	protected Task<EntityBlackboard> copyTo(Task<EntityBlackboard> task) {
		NearPlayer clone = (NearPlayer)task;
		clone.distance = distance;
		return super.copyTo(task);
	}
}
