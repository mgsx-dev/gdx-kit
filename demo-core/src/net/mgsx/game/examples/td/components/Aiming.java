package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.aim")
@EditableComponent(autoClone=true)
public class Aiming implements Component, Poolable
{
	public final static ComponentMapper<Aiming> components = ComponentMapper.getFor(Aiming.class);
	
	@Editable
	public float angleVelocity = 90;
	public float angle;
	public boolean inSights = false;
	
	@Override
	public void reset() {
		angle = 0;
		inSights = false;
	}
}
