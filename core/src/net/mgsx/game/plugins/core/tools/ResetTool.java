package net.mgsx.game.plugins.core.tools;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.tools.Tool;

public class ResetTool extends Tool
{

	public ResetTool(Editor editor) {
		super("Reset", editor);
	}
	
	@Override
	protected void activate() {
		editor.entityEngine.removeAllEntities();
		editor.selection.clear();
		editor.invalidateSelection();
		end();
	}

}
