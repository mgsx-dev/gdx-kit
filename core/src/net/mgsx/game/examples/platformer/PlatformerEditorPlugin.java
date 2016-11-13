package net.mgsx.game.examples.platformer;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.DefaultEditorPlugin;

@PluginDef(dependencies={PlatformerPlugin.class})
public class PlatformerEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		// nothing more, no platformer specific tools for now.
	}

}
