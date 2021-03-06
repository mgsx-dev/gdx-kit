package net.mgsx.game.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.screen.ScreenManager;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.storage.EntityGroupLoader;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.storage.SaveConfiguration;

public class EditorApplication extends GameApplication implements ScreenManager, KitGameListener
{
	final private EditorConfiguration config;
	
	private Engine engine;
	private EditorScreen editorScreen;
	
	public EditorApplication(EditorConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public void create() 
	{
		Gdx.input.setInputProcessor(Kit.inputs);
		
		assets = new EditorAssetManager();
		assets.setLoader(EntityGroup.class, new EntityGroupLoader(assets.getFileHandleResolver()));
		
		Texture.setAssetManager(assets);
		
		engine = new PooledEngine();
		
		// register assets manager as model accessible by its special type EditorAssetManager
		// game screen will register it again with AssetManager type.
		config.registry.registerModel(EditorAssetManager.class, (EditorAssetManager)assets);
		
		for(Plugin plugin : config.plugins){
			config.registry.registerPlugin(plugin);
		}
		
		GameScreen screen = new GameScreen(this, assets, config.registry, engine);

		// XXX fortunately, EditorScreen is not a clip so no chance to be wiped out by transitions !
		// but it might be the case if we want an editor with transitions !!!
		editorScreen = new EditorScreen(config, screen, (EditorAssetManager)assets, engine);
		
		if(config.path != null) {
			loadWork(Gdx.files.internal(config.path));
		}else{
			restoreWork();
		}
		assets.finishLoading(); // required when no restoration
		
		Kit.gameListeners.add(this);
		
		setScreen(editorScreen);
	}
	
	@Override
	public void exit(Throwable e) {
		backupWork();
	}
	
	public void backupWork() 
	{
		if(this.config.autoSavePath != null)
		{
			// save all to temp dir
			SaveConfiguration config = new SaveConfiguration();
			config.assets = assets;
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
		cfg.assets = assets;
		cfg.registry = config.registry;
		cfg.engine = engine;
		cfg.failSafe = true; 
		
		cfg.loadSettings = true;
		cfg.loadViews = true;
		
		if(file != null && file.exists()){
			EntityGroupStorage.loadForEditing(editorScreen, file.path(), cfg);
		}
	}


}
