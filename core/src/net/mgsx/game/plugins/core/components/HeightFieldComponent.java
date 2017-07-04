package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoTool = false) // TODO cloning ?
public class HeightFieldComponent implements Component, Poolable
{
	
	public final static ComponentMapper<HeightFieldComponent> components = ComponentMapper
			.getFor(HeightFieldComponent.class);
	
	public int width, height;
	public float[] values;
	
	@Override
	public void reset() {
		values = null;
		width = height = 0;
	}
}
