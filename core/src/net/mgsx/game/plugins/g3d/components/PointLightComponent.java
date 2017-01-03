package net.mgsx.game.plugins.g3d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable(value="g3d.light.point", auto=true)
@EditableComponent(autoClone=true)
public class PointLightComponent implements Component, Poolable
{
	
	public final static ComponentMapper<PointLightComponent> components = ComponentMapper.getFor(PointLightComponent.class);
	
	@Storable
	@Editable public PointLight light = new PointLight().setColor(Color.WHITE).setIntensity(100);

	@Override
	public void reset() {
		light = new PointLight().setColor(Color.WHITE).setIntensity(100);
	}
	
	
}
