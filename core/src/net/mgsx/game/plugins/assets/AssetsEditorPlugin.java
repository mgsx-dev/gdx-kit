package net.mgsx.game.plugins.assets;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.assets.editors.AssetsEditor;
import net.mgsx.game.plugins.editor.KitEditorPlugin;
import net.mgsx.game.plugins.editor.systems.EditorSystem;

@PluginDef(dependencies=KitEditorPlugin.class)
public class AssetsEditorPlugin extends EditorPlugin {

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.entityEngine.getSystem(EditorSystem.class).addGlobalEditor("Assets", new AssetsEditor());
	}

}
