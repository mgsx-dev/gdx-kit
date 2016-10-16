package net.mgsx.core.tools;

import com.badlogic.gdx.Input;

import net.mgsx.core.Editor;

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
