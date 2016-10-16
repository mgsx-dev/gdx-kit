package net.mgsx.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.plugins.Movable;

public class Attach implements Component
{
	private Entity masterEntity, slaveEntity;
	private Movable master, slave;

	private Vector3 position = new Vector3();
	
	


	public Attach(Entity masterEntity, Movable master, Entity slaveEntity, Movable slave) {
		super();
		this.masterEntity = masterEntity;
		this.master = master;
		this.slaveEntity = slaveEntity;
		this.slave = slave;
	}




	public void update()
	{
		master.getPosition(masterEntity, position);
		slave.moveTo(slaveEntity, position);
		slave.rotateTo(slaveEntity, master.getRotation(masterEntity));
	}
	

}
