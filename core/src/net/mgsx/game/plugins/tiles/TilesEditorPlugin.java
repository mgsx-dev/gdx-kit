package net.mgsx.game.plugins.tiles;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.tiles.tools.ImportAsTileTool;
import net.mgsx.game.plugins.tiles.tools.TileMapTool;

public class TilesEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		
		editor.addTool(new ImportAsTileTool(editor));
		editor.addTool(new TileMapTool(editor));
	}
	
}
