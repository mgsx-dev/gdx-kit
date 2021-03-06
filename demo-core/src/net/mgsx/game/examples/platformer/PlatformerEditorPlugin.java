package net.mgsx.game.examples.platformer;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.platformer.editors.JoystickEditor;
import net.mgsx.game.examples.platformer.inputs.JoystickController;
import net.mgsx.game.examples.platformer.tools.KeyboardPlayerTool;
import net.mgsx.game.examples.platformer.tools.PlayerDebugTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.box2d.Box2DEditorPlugin;
import net.mgsx.game.plugins.btree.BTreePlugin;
import net.mgsx.game.plugins.pd.PdEditorPlugin;

@PluginDef(dependencies={PlatformerPlugin.class, PdEditorPlugin.class, Box2DEditorPlugin.class, BTreePlugin.class})
public class PlatformerEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addTool(new PlayerDebugTool(editor));
		editor.addTool(new KeyboardPlayerTool(editor));
		
		editor.registry.registerPlugin(JoystickController.class, JoystickEditor.factory);
	}

}
