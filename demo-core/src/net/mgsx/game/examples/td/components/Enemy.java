package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.enemy")
@EditableComponent(autoClone=true)
public class Enemy implements Component, Poolable
{
	public static enum Type
	{
		TRIANGLE, SQUARE, PENTAGON, HEXAGON, CIRCLE
	}
	
	public final static ComponentMapper<Enemy> components = ComponentMapper.getFor(Enemy.class);
	
	/** distance from home */
	public float home;
	
	public transient Entity homeTarget; // XXX to say current path is targetting specific home

	@Editable
	public Type type = Type.SQUARE;

	@Override
	public void reset() {
		homeTarget = null;
	}
}
