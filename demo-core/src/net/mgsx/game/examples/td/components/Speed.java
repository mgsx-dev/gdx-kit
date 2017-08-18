package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.speed")
@EditableComponent(autoClone=true)
public class Speed implements Component {

	
	public final static ComponentMapper<Speed> components = ComponentMapper.getFor(Speed.class);
	
	@Editable
	public float base = 1;

	public transient float current;

}
