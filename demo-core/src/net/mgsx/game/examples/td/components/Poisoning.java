package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.poisoning")
@EditableComponent(autoClone=true)
public class Poisoning implements Component
{
	
	public final static ComponentMapper<Poisoning> components = ComponentMapper.getFor(Poisoning.class);
	
	@Editable
	public float damageSpeed;
	
	@Editable
	public float damageDuration;
	
}
