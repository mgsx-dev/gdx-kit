package net.mgsx.game.plugins.graphics;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.helpers.FilesShaderProgram;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.graphics.storage.FilesShaderProgramSerializer;
import net.mgsx.game.plugins.graphics.systems.ScreenSystem;

public class GraphicsPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) {
		engine.registry.addSerializer(FilesShaderProgram.class, new FilesShaderProgramSerializer());
		engine.entityEngine.addSystem(new ScreenSystem(engine));
	}

}
