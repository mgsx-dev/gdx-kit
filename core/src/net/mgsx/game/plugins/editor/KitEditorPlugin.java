package net.mgsx.game.plugins.editor;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;
import net.mgsx.game.plugins.editor.systems.EditorSystem;
import net.mgsx.game.plugins.editor.systems.HistorySystem;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;
import net.mgsx.game.plugins.editor.systems.ToolsRenderSystem;

public class KitEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.entityEngine.addSystem(new EditorSystem(editor));
		editor.entityEngine.addSystem(new SelectionSystem());
		editor.entityEngine.addSystem(new HistorySystem());
		editor.entityEngine.addSystem(new ToolsRenderSystem());
		editor.entityEngine.addSystem(new DebugRenderSystem(editor));
	}

}
