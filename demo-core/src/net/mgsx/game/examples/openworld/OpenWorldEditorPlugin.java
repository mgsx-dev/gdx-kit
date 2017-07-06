package net.mgsx.game.examples.openworld;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldDebugSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldLandRenderSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldManagerSystem;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class OpenWorldEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.entityEngine.addSystem(new OpenWorldManagerSystem());
		editor.entityEngine.addSystem(new OpenWorldDebugSystem());
		
		// TODO non edit part
		editor.entityEngine.addSystem(new OpenWorldLandRenderSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldCameraSystem(editor.game));
	}

}
