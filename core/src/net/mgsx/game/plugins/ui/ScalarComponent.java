package net.mgsx.game.plugins.ui;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class ScalarComponent implements Component
{
	
	public final static ComponentMapper<ScalarComponent> components = ComponentMapper.getFor(ScalarComponent.class);
	
	/** normalized value range from 0 to 1 */
	public float value;
}
