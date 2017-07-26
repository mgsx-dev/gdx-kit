package net.mgsx.game.plugins.graphics;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.graphics.systems.GLProfilerSystem;
import net.mgsx.game.plugins.graphics.tools.FullscreenTool;

public class GraphicsEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		
		editor.entityEngine.addSystem(new GLProfilerSystem());
		
		editor.addGlobalTool(new FullscreenTool(editor));
	}

}
