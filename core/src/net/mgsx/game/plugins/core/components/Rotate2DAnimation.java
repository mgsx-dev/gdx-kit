package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("core.2d.rotation")
@EditableComponent
public class Rotate2DAnimation implements Component {
	
	public static ComponentMapper<Rotate2DAnimation> components = ComponentMapper.getFor(Rotate2DAnimation.class);
	@Editable public float speed = 10;
	
	public float time;
}
