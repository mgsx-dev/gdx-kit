package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

// TODO speed should be in another component ? or it is a speed base used in path follower ...
@EditableComponent
public class Enemy implements Component, Poolable
{
	public static enum Type
	{
		TRIANGLE, SQUARE, PENTAGON, HEXAGON, CIRCLE
	}
	
	public final static ComponentMapper<Enemy> components = ComponentMapper.getFor(Enemy.class);
	
	/** distance from home */
	public float home;

	public float speed = 1;
	
	@Editable
	public Type type = Type.SQUARE;

	@Override
	public void reset() {
	}
}
