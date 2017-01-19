package net.mgsx.game.plugins.g3d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("g3d.light.noise")
@EditableComponent(autoClone=true)
public class LightNoiseComponent implements Component
{
	
	public final static ComponentMapper<LightNoiseComponent> components = ComponentMapper
			.getFor(LightNoiseComponent.class);
	
	@Editable
	public float frequency=1;
	
	@Editable
	public float min=0, max=1;
	
	public float offset;
}
