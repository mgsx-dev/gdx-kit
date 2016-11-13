package net.mgsx.game.plugins.ashley;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.ashley.editors.AshleyEntitiesEditor;
import net.mgsx.game.plugins.ashley.editors.AshleySystemsEditor;

public class AshleyEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addGlobalEditor("Entities", new AshleyEntitiesEditor());
		editor.addGlobalEditor("Systems", new AshleySystemsEditor());
	}
}
