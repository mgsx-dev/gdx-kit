package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoClone=true)
public class Follow implements Component
{
	
	public final static ComponentMapper<Follow> components = ComponentMapper.getFor(Follow.class);
	
	public transient Entity head;

	@Editable
	public float maxDistance, minDistance;
}
