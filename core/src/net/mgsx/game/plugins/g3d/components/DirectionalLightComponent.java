package net.mgsx.game.plugins.g3d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable(value="g3d.light.dir", auto=true)
@EditableComponent
public class DirectionalLightComponent implements Component, Poolable, Duplicable
{
	
	public final static ComponentMapper<DirectionalLightComponent> components = ComponentMapper.getFor(DirectionalLightComponent.class);
	
	@Storable
	@Editable public DirectionalLight light = new DirectionalLight().set(Color.WHITE, 0, 0, -1);
	@Editable public Quaternion direction = new Quaternion();
	
	@Storable
	@Editable public boolean shadow = false;

	@Override
	public void reset() {
		light = new DirectionalLight().set(Color.WHITE, 0, 0, -1);
		shadow = false;
	}

	@Override
	public Component duplicate(Engine engine)
	{
		DirectionalLightComponent clone = engine.createComponent(DirectionalLightComponent.class);
		clone.direction.set(direction);
		clone.light = new DirectionalLight().set(light);
		clone.shadow = shadow;
		return clone;
	}
	
}
