package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.road")
@EditableComponent(autoClone=true)
public class Road implements Component
{
	
	public final static ComponentMapper<Road> components = ComponentMapper.getFor(Road.class);
	
	/** distance from home */
	@Editable public int home;
}
