package net.mgsx.game.plugins.spline.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.annotations.Storable;

@Storable("spline")
public class PathComponent implements Component
{
	
	public static ComponentMapper<PathComponent> components = ComponentMapper.getFor(PathComponent.class);
	public Path<Vector3> path;
}
