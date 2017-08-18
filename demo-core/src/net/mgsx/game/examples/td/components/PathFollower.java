package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent(autoTool=false)
public class PathFollower implements Component, Poolable
{
	
	public final static ComponentMapper<PathFollower> components = ComponentMapper.getFor(PathFollower.class);
	
	public float t;
	
	@Editable
	public boolean loop;
	@Editable
	public boolean wrap;
	
	public transient float length;
	public transient Path<Vector2> path;
	public transient Path<Vector3> path3d;

	public float speed = 1;
	
	@Override
	public void reset() {
		t = 0;
		path = null; // OPTIM give back to pool ?
		path3d = null;
		loop = false;
		wrap = false;
		speed = 1;
	}
}
