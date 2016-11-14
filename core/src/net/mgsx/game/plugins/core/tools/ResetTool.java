package net.mgsx.game.plugins.core.tools;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;

public class ResetTool extends Tool
{

	public ResetTool(EditorScreen editor) {
		super("Reset", editor);
	}
	
	@Override
	protected void activate() {
		editor.reset();
		end();
	}

}
