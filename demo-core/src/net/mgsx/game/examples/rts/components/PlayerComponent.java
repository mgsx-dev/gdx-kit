package net.mgsx.game.examples.rts.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class PlayerComponent implements Component
{
	
	public final static ComponentMapper<PlayerComponent> components = ComponentMapper.getFor(PlayerComponent.class);
	
	public int id;

}
