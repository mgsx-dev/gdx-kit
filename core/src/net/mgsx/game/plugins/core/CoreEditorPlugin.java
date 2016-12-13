package net.mgsx.game.plugins.core;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.storage.EntityGroupRef;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.NoTool;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.plugins.camera.CameraEditorPlugin;
import net.mgsx.game.plugins.core.systems.PolygonRenderSystem;
import net.mgsx.game.plugins.core.systems.SelectionRenderSystem;
import net.mgsx.game.plugins.core.tools.CreateProxyTool;
import net.mgsx.game.plugins.core.tools.DeleteTool;
import net.mgsx.game.plugins.core.tools.DuplicateTool;
import net.mgsx.game.plugins.core.tools.EntityEmitterTool;
import net.mgsx.game.plugins.core.tools.EntityGroupEditor;
import net.mgsx.game.plugins.core.tools.FollowSelectionTool;
import net.mgsx.game.plugins.core.tools.ImportPatchTool;
import net.mgsx.game.plugins.core.tools.OpenTool;
import net.mgsx.game.plugins.core.tools.PanTool;
import net.mgsx.game.plugins.core.tools.ResetProxyTool;
import net.mgsx.game.plugins.core.tools.ResetTool;
import net.mgsx.game.plugins.core.tools.SaveTool;
import net.mgsx.game.plugins.core.tools.SelectTool;
import net.mgsx.game.plugins.core.tools.SwitchCameraTool;
import net.mgsx.game.plugins.core.tools.SwitchModeTool;
import net.mgsx.game.plugins.core.tools.ToggleHelpTool;
import net.mgsx.game.plugins.core.tools.UnproxyTool;
import net.mgsx.game.plugins.core.tools.ZoomTool;

@PluginDef(dependencies={CorePlugin.class, CameraEditorPlugin.class})
public class CoreEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		// systems
		editor.entityEngine.addSystem(new SelectionRenderSystem(editor));
		editor.entityEngine.addSystem(new PolygonRenderSystem(editor));
		
		editor.addTool(new EntityEmitterTool(editor));
		editor.addTool(new ResetProxyTool(editor));
		editor.addTool(new UnproxyTool(editor));
		
		// order is very important !
		editor.addGlobalTool(new SelectTool(editor));
		editor.addGlobalTool(new ZoomTool(editor));
		editor.addGlobalTool(new PanTool(editor));
		editor.addGlobalTool(new DuplicateTool(editor));
		editor.addGlobalTool(new FollowSelectionTool(editor));
		editor.addGlobalTool(new SwitchModeTool(editor));
		editor.addGlobalTool(new ToggleHelpTool(editor));

		editor.addGlobalTool(new SwitchCameraTool(editor));
		

		editor.addSuperTool(new NoTool("Select", editor));;
		
		editor.addSuperTool(new OpenTool(editor));;
		editor.addSuperTool(new SaveTool(editor));;
		editor.addSuperTool(new ResetTool(editor));;
		editor.addSuperTool(new DeleteTool(editor));;

		editor.addSuperTool(new ImportPatchTool(editor));;

		editor.addSuperTool(new CreateProxyTool(editor));

		LoadConfiguration config = new LoadConfiguration();
		config.assets = editor.assets;
		config.engine = editor.entityEngine;
		config.registry = editor.registry;
		
		EntityEditor.defaultTypeEditors.put(EntityGroupRef.class, new EntityGroupEditor(config));
	}

}
