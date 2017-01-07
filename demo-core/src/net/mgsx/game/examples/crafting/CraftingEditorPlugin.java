package net.mgsx.game.examples.crafting;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.pd.PdEditorPlugin;

@PluginDef(dependencies={CraftingPlugin.class, PdEditorPlugin.class})
public class CraftingEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
	}

}
