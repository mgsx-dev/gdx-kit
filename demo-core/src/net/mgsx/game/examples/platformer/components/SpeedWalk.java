package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class SpeedWalk implements Component
{
	
	public final static ComponentMapper<SpeedWalk> components = ComponentMapper.getFor(SpeedWalk.class);
	
	@Editable public float speed = 1, friction = 1;
}
