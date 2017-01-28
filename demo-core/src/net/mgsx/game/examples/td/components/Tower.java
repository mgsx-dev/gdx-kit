package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.tower")
@EditableComponent(autoClone=true)
public class Tower implements Component, Poolable
{
	
	public final static ComponentMapper<Tower> components = ComponentMapper.getFor(Tower.class);
	
	public float reload;

	@Editable
	public float reloadRequired = 1;

	@Override
	public void reset() {
		reload = 0;
		reloadRequired = 1;
	}
}
