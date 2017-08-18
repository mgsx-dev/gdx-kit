package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.target")
@EditableComponent
public class SingleTarget implements Component, Poolable
{
	
	public final static ComponentMapper<SingleTarget> components = ComponentMapper.getFor(SingleTarget.class);
	
	public transient Entity target;

	@Override
	public void reset() {
		target = null;
	}
}
