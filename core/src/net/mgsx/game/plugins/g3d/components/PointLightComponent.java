package net.mgsx.game.plugins.g3d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class PointLightComponent implements Component
{
	
	public final static ComponentMapper<PointLightComponent> components = ComponentMapper.getFor(PointLightComponent.class);
	
	@Editable public PointLight light = new PointLight();
}
