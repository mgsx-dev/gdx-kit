package net.mgsx.game.examples.platformer.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class MagnetComponent implements Component{
	
	public final static ComponentMapper<MagnetComponent> components = ComponentMapper.getFor(MagnetComponent.class);
	
	@Editable public float force = 10;
}
