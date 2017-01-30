package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

// TODO speed should be in another component ? or it is a speed base used in path follower ...
public class Enemy implements Component, Poolable
{
	
	public final static ComponentMapper<Enemy> components = ComponentMapper.getFor(Enemy.class);
	
	/** distance from home */
	public float home;

	public float speed = 1;

	@Override
	public void reset() {
	}
}
