package net.mgsx.game.plugins.camera;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.camera.systems.CameraCullingDebugSystem;
import net.mgsx.game.plugins.camera.systems.CameraDebugSystem;
import net.mgsx.game.plugins.camera.systems.ViewportDebugSystem;
import net.mgsx.game.plugins.camera.tools.Camera2DTool;
import net.mgsx.game.plugins.camera.tools.ViewportTool;

@PluginDef(dependencies={CameraPlugin.class})
public class CameraEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addTool(new Camera2DTool(editor));
		editor.addTool(new ViewportTool(editor));
		
		editor.entityEngine.addSystem(new CameraCullingDebugSystem(editor));
		editor.addSystem(new CameraDebugSystem(), false);
		editor.addSystem(new ViewportDebugSystem(), false);
	}

}
