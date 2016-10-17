package net.mgsx.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

public class Attach implements Component
{
	private Entity masterEntity, slaveEntity;
	private Movable master, slave;

	private Vector3 position = new Vector3();
	
	private Vector3 offset = new Vector3();


	public Attach(Entity masterEntity, Movable master, Entity slaveEntity, Movable slave) {
		super();
		this.masterEntity = masterEntity;
		this.master = master;
		this.slaveEntity = slaveEntity;
		this.slave = slave;
		
		Vector3 masterPosition = new Vector3();
		Vector3 slavePosition = new Vector3();
		master.getPosition(masterEntity, masterPosition);
		slave.getPosition(slaveEntity, slavePosition);
		
		offset.set(slavePosition).sub(masterPosition);
	}




	public void update()
	{
		offset.z = 0; // XXX
		master.getPosition(masterEntity, position);
		position.add(offset);
		slave.moveTo(slaveEntity, position);
		slave.rotateTo(slaveEntity, master.getRotation(masterEntity));
	}
	

}
