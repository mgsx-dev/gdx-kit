package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Input.Keys;

public class KeyboardController implements Component
{
	
	public static ComponentMapper<KeyboardController> components = ComponentMapper.getFor(KeyboardController.class);
	
	/** one of {@link Keys} */
	public int left, right, up, down, jump, grab;
}
