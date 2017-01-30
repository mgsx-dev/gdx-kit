package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Enemy implements Component, Poolable
{
	
	public final static ComponentMapper<Enemy> components = ComponentMapper.getFor(Enemy.class);
	
	/** distance from home */
	public float home;

	@Override
	public void reset() {
	}
}
