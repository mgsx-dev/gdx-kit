package net.mgsx.game.examples.platformer;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.platformer.tools.FocusCameraTool;
import net.mgsx.game.examples.platformer.tools.KeyboardPlayerTool;
import net.mgsx.game.examples.platformer.tools.PlayerDebugTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;

@PluginDef(dependencies={PlatformerPlugin.class})
public class PlatformerEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addTool(new PlayerDebugTool(editor));
		editor.addTool(new KeyboardPlayerTool(editor));
		editor.addTool(new FocusCameraTool(editor));
	}

}
