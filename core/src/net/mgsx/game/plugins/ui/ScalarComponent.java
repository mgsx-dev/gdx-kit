package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("ui.scalar")
@EditableComponent
public class ScalarComponent implements Component
{
	
	public final static ComponentMapper<ScalarComponent> components = ComponentMapper.getFor(ScalarComponent.class);
	
	@Editable
	public String id = "";
	
	/** normalized value range from 0 to 1 */
	@Editable
	public float value;
}
