package net.mgsx.game.examples.tactics;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.tactics.tools.MapGeneratorTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class TacticsPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addTool(editor.defaultTool = new MapGeneratorTool(editor));
	}

}
