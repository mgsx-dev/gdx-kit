package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EnumType;

public class KeyboardController implements Component
{
	
	public static ComponentMapper<KeyboardController> components = ComponentMapper.getFor(KeyboardController.class);
	
	/** one of {@link Keys} */
	@Editable(type=EnumType.GDX_KEYS) 
	public int left = Input.Keys.LEFT, 
		 	right = Input.Keys.RIGHT, 
			up = Input.Keys.UP, 
			down = Input.Keys.DOWN,
			jump = Input.Keys.Z, 
			grab = Input.Keys.A;
}
