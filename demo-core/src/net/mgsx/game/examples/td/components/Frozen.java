package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoClone=true)
public class Frozen implements Component
{
	
	public final static ComponentMapper<Frozen> components = ComponentMapper.getFor(Frozen.class);
	
	public float rate, timeout;
}
