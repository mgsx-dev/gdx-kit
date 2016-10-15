package net.mgsx.core.tools;

import net.mgsx.core.Editor;

public class EditorTool extends Tool
{
	final protected Editor editor;
	public EditorTool(String name, Editor editor) {
		super(name, editor.orthographicCamera);
		this.editor = editor;
	}

}
