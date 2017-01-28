package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.tile")
@EditableComponent(autoClone=true)
public class TileComponent implements Component
{
	
	public final static ComponentMapper<TileComponent> components = ComponentMapper.getFor(TileComponent.class);
	
	@Editable public int x, y;

	/** distance from home */
	@Editable public int home;
}
