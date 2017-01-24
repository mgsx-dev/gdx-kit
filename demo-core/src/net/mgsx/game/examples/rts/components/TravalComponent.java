package net.mgsx.game.examples.rts.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class TravalComponent implements Component {

	
	public final static ComponentMapper<TravalComponent> components = ComponentMapper.getFor(TravalComponent.class);
	public Entity srcPlanet;
	public Entity dstPlanet;
}
