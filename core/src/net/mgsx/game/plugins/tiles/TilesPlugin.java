package net.mgsx.game.plugins.tiles;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.tiles.systems.TileMapSystem;

@PluginDef
public class TilesPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) {
		engine.entityEngine.addSystem(new TileMapSystem(engine));
	}

}
