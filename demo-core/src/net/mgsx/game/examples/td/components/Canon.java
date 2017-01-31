package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

// TODO split in load, damage components and just keep aiming behavior (rotation look at)
@Storable("td.canon")
@EditableComponent(autoClone=true)
public class Canon implements Component, Poolable
{
	public final static ComponentMapper<Canon> components = ComponentMapper.getFor(Canon.class);
	
	public float reload;

	@Editable
	public float reloadRequired = 1;

	@Editable
	public float damages = 1;
	
	@Editable
	public float angleVelocity = 90;
	public float angle;
	
	@Override
	public void reset() {
		reload = 0;
		reloadRequired = 1;
	}
}
