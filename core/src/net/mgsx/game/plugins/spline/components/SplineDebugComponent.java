package net.mgsx.game.plugins.spline.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class SplineDebugComponent implements Component
{
	
	public static ComponentMapper<SplineDebugComponent> components = ComponentMapper.getFor(SplineDebugComponent.class);
	public Vector3[] vertices;
	public boolean dirty;
}
