package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.KeyTool;

public class ToggleHelpTool extends KeyTool
{
	public ToggleHelpTool(EditorScreen editor) 
	{
		super(editor, Input.Keys.F1);
	}

	@Override
	protected void run() {
		editor.toggleStatus();
	}
	
}
