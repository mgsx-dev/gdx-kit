package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class Poisoned implements Component
{
	
	public final static ComponentMapper<Poisoned> components = ComponentMapper.getFor(Poisoned.class);
	
	@Editable
	public float damageSpeed;
	
	@Editable
	public float duration;

}
