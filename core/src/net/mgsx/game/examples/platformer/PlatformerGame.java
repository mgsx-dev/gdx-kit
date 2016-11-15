package net.mgsx.game.examples.platformer;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.GameScreen;

public class PlatformerGame extends Game
{
	private AssetManager assets;
	private Engine engine;
	
	@Override
	public void create() 
	{
		// For now it's just a single screen game, menu, intro and transitions
		// will come later.
		// we boot up on level 1.
		assets = new AssetManager();
		Texture.setAssetManager(assets);

		engine = new PooledEngine();
		
		GameScreen screen = new GameScreen(assets, engine);
		screen.registry = new GameRegistry();
		screen.registry.registerPlugin(new PlatformerPlugin());
		screen.registry.init(screen);
		
		screen.load(Gdx.files.internal("levels/level1.json"));
		
		setScreen(screen);
	}

}
