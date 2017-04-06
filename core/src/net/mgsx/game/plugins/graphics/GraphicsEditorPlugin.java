package net.mgsx.game.plugins.graphics;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.graphics.systems.GLProfilerSystem;

public class GraphicsEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		
		editor.entityEngine.addSystem(new GLProfilerSystem());
	}

}
