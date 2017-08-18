package net.mgsx.game.examples.quantum.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoClone=true, autoTool=false)
public class Link implements Component
{
	
	public final static ComponentMapper<Link> components = ComponentMapper.getFor(Link.class);
	
	public Entity source, target;
}
