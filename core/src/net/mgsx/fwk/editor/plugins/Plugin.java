package net.mgsx.fwk.editor.plugins;

import net.mgsx.fwk.editor.Editor;

public class Plugin 
{
	final protected Editor editor;
	
	public Plugin(Editor editor) {
		super();
		this.editor = editor;
	}

	/**
	 * build all necessary and add features to editor.
	 */
	public void initialize() 
	{
	}

}
