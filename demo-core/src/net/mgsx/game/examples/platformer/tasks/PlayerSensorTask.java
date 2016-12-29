package net.mgsx.game.examples.platformer.tasks;

import net.mgsx.game.examples.platformer.physics.PlayerPhysicSensor;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;

@TaskAlias("playerSensor")
public class PlayerSensorTask extends ConditionTask
{
	@Override
	public void start() {
		// ensure entity has player physic sensor
		// TODO maybe check contact list for init in/out ?
		if(!PlayerPhysicSensor.components.has(getEntity())){
			getEntity().add(getEngine().createComponent(PlayerPhysicSensor.class));
		}
	}
	
	@Override
	public boolean match() {
		PlayerPhysicSensor sensor = PlayerPhysicSensor.components.get(getEntity());
		if(sensor != null){
			return sensor.inside;
		}
		return false;
	}
	

}
