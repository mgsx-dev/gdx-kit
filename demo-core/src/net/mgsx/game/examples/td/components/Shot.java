package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Shot implements Component, Poolable
{
	
	public final static ComponentMapper<Shot> components = ComponentMapper.getFor(Shot.class);
	
	public Vector2 start = new Vector2();
	public Vector2 end = new Vector2();
	public float t;
	public float speed = 5;
	@Override
	public void reset() {
		t = 0;
	}

}
