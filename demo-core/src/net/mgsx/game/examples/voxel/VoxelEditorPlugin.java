package net.mgsx.game.examples.voxel;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.pd.PdEditorPlugin;

@PluginDef(dependencies={VoxelPlugin.class, PdEditorPlugin.class})
public class VoxelEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
	}

}
