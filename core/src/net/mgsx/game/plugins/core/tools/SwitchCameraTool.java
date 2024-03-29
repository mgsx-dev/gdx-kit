package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;

//TODO merge to SwitchCameraTool2 with binding configuration ...
public class SwitchCameraTool extends Tool {
	public SwitchCameraTool(EditorScreen editor) {
		super("Camera switch", editor);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.NUMPAD_0 || keycode == Input.Keys.INSERT){
			editor.getEditorCamera().switchCameras();
			return true;
		}
		return super.keyDown(keycode);
	}
}