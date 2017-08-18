package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.stunning")
@EditableComponent(autoClone=true)
public class Stunning implements Component
{
	
	public final static ComponentMapper<Stunning> components = ComponentMapper.getFor(Stunning.class);
	
	@Editable
	public float duration;
	
	// TODO maybe separate chance and stunning : chance could be applyied on any shooter !
	@Editable(type=EnumType.UNIT)
	public float chance;
}
