package net.mgsx.game.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.EngineStorage;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.storage.EntityGroupLoader;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.storage.SaveConfiguration;

public class EditorApplication extends Game
{
	final private EditorConfiguration config;
	
	protected EditorAssetManager assetManager;
	private Engine engine;
	private EditorScreen editorScreen;
	
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
		
		editorScreen = new EditorScreen(config, screen, assetManager, engine);
		
		if(config.path != null) {
			editorScreen.loadForEditing(Gdx.files.absolute(config.path));
		}else{
			restoreWork();
		}
		
		setScreen(editorScreen);
	}
	
	@Override
	public void render() {
		try{
			super.render();
		}catch(Error e){
			backupWork();
			throw e;
		}
	}
	
	public void backupWork() {
		// save all to temp dir
		SaveConfiguration config = new SaveConfiguration();
		config.assets = assetManager;
		config.engine = engine;
		config.registry = this.config.registry;
		
		config.pretty = true;
		config.stripPaths = true;
		
		EntityGroupStorage.save(Gdx.files.absolute("/tmp/entities.json"), config); // TODO linux only
		
		config.visibleSystems = editorScreen.pinnedSystems;
		
		EngineStorage.save(Gdx.files.absolute("/tmp/settings.json"), config); // TODO linux only
	}
	
	private void restoreWork(){
		FileHandle recovery = Gdx.files.absolute("/tmp/entities.json"); // TODO linux only
		if(recovery.exists()){
			editorScreen.loadForEditing(recovery);
		}
		FileHandle settingsRecovery = Gdx.files.absolute("/tmp/settings.json"); // TODO linux only
		if(settingsRecovery.exists()){
			LoadConfiguration cfg = new LoadConfiguration();
			cfg.assets = assetManager;
			cfg.registry = config.registry;
			cfg.engine = engine;
			cfg.failSafe = true; 
			EngineStorage.load(settingsRecovery, cfg);
			editorScreen.pinEditors(cfg.visibleSystems);
		}
	}

	@Override
	public void dispose() 
	{
		backupWork();
		
		super.dispose();
	}

}
