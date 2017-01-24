package net.mgsx.game.examples.rts.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class RoadComponent implements Component {
	
	public final static ComponentMapper<RoadComponent> components = ComponentMapper.getFor(RoadComponent.class);
	
	public Entity srcPlanet, dstPlanet;
}
