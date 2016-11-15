package net.mgsx.game.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.plugins.Plugin;

public class EditorApplication extends Game
{
	final private EditorConfiguration config;
	
	private EditorAssetManager assetManager;
	private Engine engine;
	
	public EditorApplication(EditorConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public void create() 
	{
		assetManager = new EditorAssetManager();
		
		engine = new PooledEngine(); // TODO maybe not : pol can mess with some editor workflow (history undo)
		
		for(Plugin plugin : config.plugins){
			config.registry.registerPlugin(plugin);
		}
		
		GameScreen screen = new GameScreen(assetManager, engine);

		screen.registry = config.registry;
		
		config.registry.init(screen);
		
		EditorScreen editorScreen = new EditorScreen(config, screen, assetManager, engine);
		
		if(config.path != null && config.root != null) {
			screen.load(Gdx.files.absolute(config.root).child(config.path));
		}
		
		setScreen(editorScreen);
	}

}
