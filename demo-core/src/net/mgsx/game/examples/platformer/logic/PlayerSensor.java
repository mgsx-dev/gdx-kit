package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class PlayerSensor implements Component{

	
	public final static ComponentMapper<PlayerSensor> components = ComponentMapper.getFor(PlayerSensor.class);
	
	public float distance;
	public boolean exists;
}
