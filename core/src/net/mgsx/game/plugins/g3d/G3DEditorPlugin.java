package net.mgsx.game.plugins.g3d;

import com.badlogic.gdx.graphics.g3d.Model;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.editors.G3DNodeEditor;
import net.mgsx.game.plugins.g3d.systems.G3DBoundaryDebugSystem;
import net.mgsx.game.plugins.g3d.systems.G3DPointLightDebugSystem;
import net.mgsx.game.plugins.g3d.systems.G3DProfilerSystem;
import net.mgsx.game.plugins.g3d.tools.AddModelTool;
import net.mgsx.game.plugins.g3d.tools.G3DModelReloader;
import net.mgsx.game.plugins.g3d.tools.ModelSelector;

@PluginDef(dependencies={G3DPlugin.class})
public class G3DEditorPlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		// tools
		editor.addTool(new AddModelTool(editor));
		editor.registry.registerPlugin(G3DModel.class, new G3DNodeEditor());
		editor.addSelector(new ModelSelector(editor));
		editor.assets.addReloadListener(Model.class, new G3DModelReloader(editor.entityEngine));
		
		// systems
		editor.entityEngine.addSystem(new G3DBoundaryDebugSystem(editor));
		editor.entityEngine.addSystem(new G3DPointLightDebugSystem(editor));
		editor.entityEngine.addSystem(new G3DProfilerSystem());
	}
}
