package net.mgsx.game.plugins.controller.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Input;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EnumType;

@EditableComponent
public class MoveControl implements Component
{
	public final static ComponentMapper<MoveControl> components = ComponentMapper.getFor(MoveControl.class);
	
	@Editable(type=EnumType.GDX_KEYS) 
	public int leftKey = Input.Keys.LEFT, 
		 	rightKey = Input.Keys.RIGHT, 
			upKey = Input.Keys.UP, 
			downKey = Input.Keys.DOWN;

	@Editable public float speed = 1;
	
}
