package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.life")
@EditableComponent(autoClone=true)
public class Life implements Component, Poolable
{
	
	public final static ComponentMapper<Life> components = ComponentMapper.getFor(Life.class);
	
	@Editable
	public float current = 0, max = 1;

	@Override
	public void reset() {
		// default values to prevent division by zero and negative values
		current = 0;
		max = 1;
	}
}
