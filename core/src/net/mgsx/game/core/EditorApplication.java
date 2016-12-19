package net.mgsx.game.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.storage.EntityGroupLoader;

public class EditorApplication extends Game
{
	final private EditorConfiguration config;
	
	protected EditorAssetManager assetManager;
	private Engine engine;
	
	public EditorApplication(EditorConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public void create() 
	{
		assetManager = new EditorAssetManager();
		assetManager.setLoader(EntityGroup.class, new EntityGroupLoader(assetManager.getFileHandleResolver(), config.registry));
		
		Texture.setAssetManager(assetManager);
		
		engine = new PooledEngine(); // TODO maybe not : pol can mess with some editor workflow (history undo)
		
		for(Plugin plugin : config.plugins){
			config.registry.registerPlugin(plugin);
		}
		
		GameScreen screen = new GameScreen(assetManager, engine);

		screen.registry = config.registry;
		
		config.registry.init(screen);
		
		EditorScreen editorScreen = new EditorScreen(config, screen, assetManager, engine);
		
		if(config.path != null) {
			editorScreen.loadForEditing(Gdx.files.absolute(config.path));
		}
		
		setScreen(editorScreen);
	}

}
