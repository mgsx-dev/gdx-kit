package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class SingleTarget implements Component
{
	
	public final static ComponentMapper<SingleTarget> components = ComponentMapper.getFor(SingleTarget.class);
	
	public Entity target;
}
