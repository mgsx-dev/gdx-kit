package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.multi-target")
@EditableComponent
public class MultiTarget implements Component
{
	
	public final static ComponentMapper<MultiTarget> components = ComponentMapper.getFor(MultiTarget.class);
	
	@Editable
	public int max = 1;
	
	public transient Array<Entity> targets = new Array<Entity>();
}
