package net.mgsx.game.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.plugins.Plugin;
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
		assetManager.setLoader(EntityGroup.class, new EntityGroupLoader(assetManager.getFileHandleResolver()));
		
		Texture.setAssetManager(assetManager);
		
		engine = new PooledEngine();
		
		for(Plugin plugin : config.plugins){
			config.registry.registerPlugin(plugin);
		}
		
		GameScreen screen = new GameScreen(assetManager, config.registry, engine);

		editorScreen = new EditorScreen(config, screen, assetManager, engine);
		
		if(config.path != null) {
			loadWork(Gdx.files.internal(config.path));
		}else{
			restoreWork();
		}
		
		setScreen(editorScreen);
	}
	
	@Override
	public void render() {
		try{
			super.render();
		}catch(Throwable e){
			backupWork();
			throw new GdxRuntimeException(e);
		}
	}
	
	public void backupWork() 
	{
		if(this.config.autoSavePath != null)
		{
			// save all to temp dir
			SaveConfiguration config = new SaveConfiguration();
			config.assets = assetManager;
			config.engine = engine;
			config.registry = this.config.registry;
			
			config.pretty = true;
			config.stripPaths = true;
			
			config.saveSystems = true;
			config.saveViews = true;
			
			config.visibleSystems = editorScreen.pinnedSystems;
			
			EntityGroupStorage.save(Gdx.files.local(this.config.autoSavePath), config);
		}
	}
	private void restoreWork()
	{
		if(config.autoSavePath != null)
		{
			loadWork(Gdx.files.internal(config.autoSavePath));
		}
	}
	private void loadWork(FileHandle file)
	{
		final LoadConfiguration cfg = new LoadConfiguration();
		cfg.assets = assetManager;
		cfg.registry = config.registry;
		cfg.engine = engine;
		cfg.failSafe = true; 
		
		cfg.loadSettings = true;
		cfg.loadViews = true;
		
		if(file != null && file.exists()){
			EntityGroupStorage.loadForEditing(editorScreen, file.path(), cfg);
		}
	}

	@Override
	public void dispose() 
	{
		backupWork();
		
		super.dispose();
	}

}
