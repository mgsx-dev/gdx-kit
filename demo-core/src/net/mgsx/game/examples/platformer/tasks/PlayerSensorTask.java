package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.physics.PlayerPhysicSensor;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("playerSensor")
public class PlayerSensorTask extends EntityLeafTask
{
	@Override
	public void start() {
		// ensure entity has player physic sensor
		// TODO maybe check contact list for init in/out ?
		getEntity().add(getEngine().createComponent(PlayerPhysicSensor.class));
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() {
		PlayerPhysicSensor sensor = PlayerPhysicSensor.components.get(getEntity());
		if(sensor != null && sensor.inside){
			sensor.inside = false;
			return Status.SUCCEEDED;
		}
		return Status.RUNNING;
	}
	
	@Override
	public void end() {
		getEntity().remove(PlayerPhysicSensor.class);
		super.end();
	}
	

}
