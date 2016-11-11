package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class PolygonComponent implements Component, Poolable
{
	public static ComponentMapper<PolygonComponent> components = ComponentMapper.getFor(PolygonComponent.class);
	
	public Array<Vector2> vertex = new Array<Vector2>();

	@Editable public boolean loop;

	@Override
	public void reset() {
		vertex.clear();
		loop = false;
	}
}
