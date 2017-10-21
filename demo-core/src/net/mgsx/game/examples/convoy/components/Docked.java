package net.mgsx.game.examples.convoy.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class Docked implements Component
{
	
	public final static ComponentMapper<Docked> components = ComponentMapper.getFor(Docked.class);
	
	public Entity planet;
}
