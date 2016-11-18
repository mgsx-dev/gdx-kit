package net.mgsx.game.plugins.boundary;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.boundary.systems.BoundaryDebugSystem;
import net.mgsx.game.plugins.boundary.tools.ManualBoundaryTool;

@PluginDef(dependencies={BoundaryPlugin.class})
public class BoundaryEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(final EditorScreen editor) 
	{
		
		editor.addTool(new ManualBoundaryTool(editor));
		
		editor.entityEngine.addSystem(new BoundaryDebugSystem(editor));

	}
}
