package net.mgsx.game.plugins.procedural;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.procedural.systems.GasRenderSystem;
import net.mgsx.game.plugins.procedural.systems.HeightFieldDebugSystem;
import net.mgsx.game.plugins.procedural.tools.ProceduralHeightFieldTool;

@PluginDef
public class ProceduralEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addTool(new ProceduralHeightFieldTool(editor));
		
		editor.entityEngine.addSystem(new HeightFieldDebugSystem());
		editor.entityEngine.addSystem(new GasRenderSystem(editor));
	}

}
