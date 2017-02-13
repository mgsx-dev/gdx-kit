package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;

// TODO separate switch mode and camera ? add a GUI for it ?
public class SwitchModeTool extends Tool
{
	public SwitchModeTool(EditorScreen editor) {
		super("Switches", editor);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.TAB){
			editor.switchVisibility();
			return true;
		}
		else if(keycode == Input.Keys.F3)
		{
			editor.getEditorCamera().switchCamera();
		}
		return super.keyDown(keycode);
	}
	
}
