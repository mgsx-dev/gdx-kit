package net.mgsx.game.examples.rts.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class SunComponent implements Component
{
	
	public final static ComponentMapper<SunComponent> components = ComponentMapper.getFor(SunComponent.class);
	
	@Editable
	public float size;

	@Editable
	public Color color = new Color(Color.WHITE);
}
