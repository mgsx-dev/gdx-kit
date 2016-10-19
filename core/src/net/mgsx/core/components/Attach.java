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
		
		Vector3 origin = new Vector3();
		slave.getOrigin(slaveEntity, origin);
		
		offset.set(slavePosition).sub(masterPosition);

		origin.add(offset).scl(-1);
		slave.setOrigin(slaveEntity, origin);
	}




	public void update()
	{
		offset.z = 1; // XXX
		master.getPosition(masterEntity, position);
		position.add(offset);
		slave.moveTo(slaveEntity, position);
		
		// TODO option attach position only !
		slave.rotateTo(slaveEntity, master.getRotation(masterEntity));
	}
	

}
