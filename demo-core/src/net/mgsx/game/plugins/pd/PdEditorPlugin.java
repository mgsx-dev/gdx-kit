package net.mgsx.game.plugins.pd;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.pd.editors.PdEditor;

@PluginDef
public class PdEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.registry.addGlobalEditor("Pd", new PdEditor());
	}

}
