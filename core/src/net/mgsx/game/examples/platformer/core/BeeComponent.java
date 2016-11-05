package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class BeeComponent implements Component
{
	public static final ComponentMapper<BeeComponent> components = ComponentMapper.getFor(BeeComponent.class);
	
	public float life;
}
