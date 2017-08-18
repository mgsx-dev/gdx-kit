package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.range")
@EditableComponent(autoClone=true)
public class Range implements Component
{
	
	public final static ComponentMapper<Range> components = ComponentMapper.getFor(Range.class);
	
	@Editable
	public float distance = 4;

}
