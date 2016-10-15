package net.mgsx.plugins.box2d.tools;

import net.mgsx.core.Editor;
import net.mgsx.core.tools.EditorTool;
import net.mgsx.core.tools.Tool;

public class NoTool extends EditorTool
{
	
	public NoTool(String name, Editor editor) {
		super(name, editor);
	}

	@Override
	protected void activate() {
		editor.subToolGroup.clear();
	}

}
