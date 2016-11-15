package net.mgsx.game.plugins.assets;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.assets.editors.AssetsEditor;

public class AssetsEditorPlugin extends EditorPlugin {

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.registry.addGlobalEditor("Assets", new AssetsEditor());
	}

}
