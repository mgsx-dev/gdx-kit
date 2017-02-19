package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class Direction implements Component
{
	
	public final static ComponentMapper<Direction> components = ComponentMapper.getFor(Direction.class);
	
	@Editable
	public Vector2 vector = new Vector2();
}
