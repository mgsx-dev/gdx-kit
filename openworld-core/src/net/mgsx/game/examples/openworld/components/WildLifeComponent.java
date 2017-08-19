package net.mgsx.game.examples.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class WildLifeComponent implements Component
{
	
	public final static ComponentMapper<WildLifeComponent> components = ComponentMapper.getFor(WildLifeComponent.class);
	
	public float time;
	public float speed;
}
