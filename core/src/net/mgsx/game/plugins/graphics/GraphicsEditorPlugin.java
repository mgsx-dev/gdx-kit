package net.mgsx.game.plugins.graphics;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.FilesShaderProgram;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.graphics.storage.FilesShaderProgramSerializer;
import net.mgsx.game.plugins.graphics.systems.GLProfilerSystem;

public class GraphicsEditorPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		
		editor.entityEngine.addSystem(new GLProfilerSystem());
		
		editor.registry.addSerializer(FilesShaderProgram.class, new FilesShaderProgramSerializer());
	}

}
