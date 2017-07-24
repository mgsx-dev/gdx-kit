package net.mgsx.game.plugins.graphics;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.graphics.systems.ScreenSystem;

public class GraphicsPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) {
		engine.entityEngine.addSystem(new ScreenSystem(engine));
	}

}
