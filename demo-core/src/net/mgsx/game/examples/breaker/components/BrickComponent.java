package net.mgsx.game.examples.breaker.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class BrickComponent implements Component
{
	
	public final static ComponentMapper<BrickComponent> components = ComponentMapper.getFor(BrickComponent.class);
	
	public int life;
	
	public boolean solid;
	
	
	
}
