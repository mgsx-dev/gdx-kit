package net.mgsx.game.plugins.spline.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(name="Spline Debug")
public class SplineDebugComponent implements Component, Poolable
{
	
	public static ComponentMapper<SplineDebugComponent> components = ComponentMapper.getFor(SplineDebugComponent.class);
	public Vector3[] vertices = null;
	public boolean dirty = true;
	
	@Override
	public void reset() {
		vertices = null;
		dirty = true;
	}
}
