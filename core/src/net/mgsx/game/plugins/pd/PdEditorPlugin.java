package net.mgsx.game.plugins.pd;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;

@PluginDef(dependencies=PdPlugin.class,
requires="net.mgsx.pd.Pd")
public class PdEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
	}

}
