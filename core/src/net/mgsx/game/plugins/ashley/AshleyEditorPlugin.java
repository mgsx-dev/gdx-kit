package net.mgsx.game.plugins.ashley;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.ashley.editors.AshleyEntitiesEditor;
import net.mgsx.game.plugins.ashley.editors.AshleySystemsEditor;

// TODO plugin to display entities and systems
public class AshleyEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(Editor editor) 
	{
		editor.addGlobalEditor("Entities", new AshleyEntitiesEditor());
		editor.addGlobalEditor("Systems", new AshleySystemsEditor());
	}
}
