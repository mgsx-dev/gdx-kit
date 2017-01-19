package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoClone=true)
public class Hidden implements Component
{
	
	public final static ComponentMapper<Hidden> components = ComponentMapper.getFor(Hidden.class);
}
