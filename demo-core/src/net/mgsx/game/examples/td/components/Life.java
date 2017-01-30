package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.life")
@EditableComponent(autoClone=true)
public class Life implements Component
{
	
	public final static ComponentMapper<Life> components = ComponentMapper.getFor(Life.class);
	
	@Editable
	public float current, max;
}
