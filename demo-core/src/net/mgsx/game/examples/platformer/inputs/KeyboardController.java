package net.mgsx.game.examples.platformer.inputs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Input;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.keyboard")
@EditableComponent(autoClone=true)
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
