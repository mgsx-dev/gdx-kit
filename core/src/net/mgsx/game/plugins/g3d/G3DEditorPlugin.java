package net.mgsx.game.plugins.g3d;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.editors.G3DEditor;
import net.mgsx.game.plugins.g3d.editors.G3DNodeEditor;
import net.mgsx.game.plugins.g3d.systems.G3DBoundaryDebugSystem;
import net.mgsx.game.plugins.g3d.tools.AddModelTool;
import net.mgsx.game.plugins.g3d.tools.ModelSelector;

@PluginDef(dependencies={G3DPlugin.class})
public class G3DEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		// tools
		editor.registry.addGlobalEditor("G3D", new G3DEditor());
		editor.addTool(new AddModelTool(editor));
		editor.registry.registerPlugin(G3DModel.class, new G3DNodeEditor());
		editor.addSelector(new ModelSelector(editor));
		
		// systems
		editor.entityEngine.addSystem(new G3DBoundaryDebugSystem(editor));
	}
}
