package net.mgsx.game.examples.platformer.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class LiquidComponent implements Component {

	
	public final static ComponentMapper<LiquidComponent> components = ComponentMapper.getFor(LiquidComponent.class);
	
	@Editable
	public float density = 1;
}
