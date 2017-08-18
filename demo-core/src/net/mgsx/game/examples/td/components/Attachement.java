package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.ContextualDuplicable;
import net.mgsx.game.core.storage.EntityGroup;

@Storable("td.attachement")
@EditableComponent(autoClone=true, autoTool=false)
public class Attachement implements Component, Poolable, ContextualDuplicable
{
	
	public final static ComponentMapper<Attachement> components = ComponentMapper.getFor(Attachement.class);
	
	@Editable
	public Vector2 offset = new Vector2();
	
	public Entity parent;

	@Override
	public void reset() {
		offset.setZero();
		// TODO could call detach on parent ?
		parent = null;
	}

	@Override
	public Component duplicate(Engine engine, EntityGroup sourceGroup, EntityGroup cloneGroup) {
		Attachement clone = engine.createComponent(Attachement.class);
		clone.offset.set(this.offset);
		int index = sourceGroup.entities().indexOf(parent, true);
		if(index >= 0) clone.parent = cloneGroup.get(index);
		return clone;
	}
}
