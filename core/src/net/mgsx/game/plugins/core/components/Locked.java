package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class Locked implements Component
{
	
	public final static ComponentMapper<Locked> components = ComponentMapper.getFor(Locked.class);
}
