package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;

public class UndoTool extends Tool
{
	public UndoTool(EditorScreen editor) {
		super("undo redo", editor);
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.Z)
		{
			if(ctrl())
			{
				if(shift()){
					historySystem.history.redo();
				}else{
					historySystem.history.undo();
				}
				return true;
			}
		}
		return false;
	}
}
