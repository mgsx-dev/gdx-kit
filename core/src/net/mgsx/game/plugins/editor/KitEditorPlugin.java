package net.mgsx.game.plugins.editor;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.editor.systems.EditorSystem;

public class KitEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.entityEngine.addSystem(new EditorSystem());
	}

}
