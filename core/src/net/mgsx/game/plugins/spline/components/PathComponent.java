package net.mgsx.game.plugins.spline.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Duplicable;

@Storable("spline")
public class PathComponent implements Component, Duplicable, Disposable
{
	
	public static ComponentMapper<PathComponent> components = ComponentMapper.getFor(PathComponent.class);
	
	public Path<Vector3> path;

	/** 
	 * optional precomputed path length.
	 * may be set at component creation time by user code.
	 * this value is mainly used for linear path following algorithms. 
	 * Default is zero.
	 */
	public float length;

	@Override
	public Component duplicate(Engine engine) {
		PathComponent clone = engine.createComponent(PathComponent.class);
		clone.path = path; // XXX by reference !
		clone.length = length;
		return clone;
	}

	@Override
	public void dispose() {
		length = 0;
	}
}
