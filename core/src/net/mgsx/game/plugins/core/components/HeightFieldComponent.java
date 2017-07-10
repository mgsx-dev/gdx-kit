package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoTool = false) // TODO cloning ?
public class HeightFieldComponent implements Component, Poolable
{
	
	public final static ComponentMapper<HeightFieldComponent> components = ComponentMapper
			.getFor(HeightFieldComponent.class);
	
	public int width, height;
	// values
	public float[] values;
	/** extra values : contains values and extra lines and rows
		this array is width+2 and height+2. */
	public float[] extraValues;
	public Vector3 [] normals;

	public Vector3 position = new Vector3();
	
	@Override
	public void reset() {
		values = null;
		normals = null;
		width = height = 0;
	}
}
