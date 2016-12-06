package net.mgsx.game.examples.platformer.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.examples.platformer.inputs.KeyboardController;
import net.mgsx.game.examples.platformer.inputs.PlayerController;

public class KeyboardPlayerTool extends Tool {
	public KeyboardPlayerTool(EditorScreen editor) {
		super("Keyboard Player", editor);
	}

	@Override
	protected void activate() 
	{
		Entity entity = editor.currentEntity();
		
		entity.add(editor.entityEngine.createComponent(PlayerController.class));
		KeyboardController keys = editor.entityEngine.createComponent(KeyboardController.class);
		keys.up = Input.Keys.UP;
		keys.down = Input.Keys.DOWN;
		keys.left = Input.Keys.LEFT;
		keys.right = Input.Keys.RIGHT;

		keys.jump = Input.Keys.Z;
		keys.grab = Input.Keys.A;
		entity.add(keys);
		
		end();
	}
}