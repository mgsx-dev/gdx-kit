package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.shooter")
@EditableComponent(autoClone=true)
public class Shooter implements Component
{
	public final static ComponentMapper<Shooter> components = ComponentMapper.getFor(Shooter.class);
	
	@Editable
	public int maxShots = 1;
}
