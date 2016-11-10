package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Input;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.tools.Tool;

public class UndoTool extends Tool
{
	public UndoTool(Editor editor) {
		super("undo redo", editor);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.Z)
		{
			if(ctrl())
			{
				if(shift()){
					editor.history.redo();
				}else{
					editor.history.undo();
				}
				return true;
			}
		}
		return false;
	}
}
