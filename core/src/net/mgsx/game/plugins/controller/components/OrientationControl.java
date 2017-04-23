package net.mgsx.game.plugins.controller.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("control.orientation")
@EditableComponent(autoClone=true)
public class OrientationControl implements Component
{
	
	public final static ComponentMapper<OrientationControl> components = ComponentMapper
			.getFor(OrientationControl.class);
	
	@Editable
	public float angleSpeed = 90;
}
