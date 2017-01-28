package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PathFollower implements Component, Poolable
{
	
	public final static ComponentMapper<PathFollower> components = ComponentMapper.getFor(PathFollower.class);
	
	public float t;
	public float speed;
	
	public int sx, sy, tx, ty;

	@Override
	public void reset() {
		t = 0;
	}
}
