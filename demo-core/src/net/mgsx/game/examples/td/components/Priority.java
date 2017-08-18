package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.priority")
@EditableComponent(autoClone=true)
public class Priority implements Component, Poolable
{
	
	public final static ComponentMapper<Priority> components = ComponentMapper.getFor(Priority.class);
	
	public static enum Rule
	{
		DANGEROUS, CLOSE, FAR, WEAK, STRONG, RANDOM
	}
	
	@Editable
	public Rule rule = Rule.DANGEROUS;
	
	public Rule current;

	@Override
	public void reset() {
		current = null;
		rule = Rule.DANGEROUS;
	}
}
