package net.mgsx.game.core.tools;

import net.mgsx.game.core.Editor;

public class NoTool extends Tool
{
	
	public NoTool(String name, Editor editor) {
		super(name, editor);
	}

	@Override
	protected void activate() {
		editor.subToolGroup.clear();
	}

}
