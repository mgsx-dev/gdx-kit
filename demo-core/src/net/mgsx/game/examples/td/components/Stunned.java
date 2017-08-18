package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class Stunned implements Component
{
	
	public final static ComponentMapper<Stunned> components = ComponentMapper.getFor(Stunned.class);
	
	public transient float duration;
}
