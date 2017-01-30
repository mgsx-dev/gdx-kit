package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.damage")
@EditableComponent(autoClone=true)
public class Damage implements Component
{
	
	public final static ComponentMapper<Damage> components = ComponentMapper.getFor(Damage.class);
	
	@Editable
	public float amount;
}
