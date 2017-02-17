package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GameRegistry;

public class LoadConfiguration {

	public AssetManager assets;
	public GameRegistry registry;
	public Engine engine;
	
	// set default load entities only (for backward compatibility)
	public boolean loadEntities = true;
	public boolean loadSettings = false;
	public boolean loadViews = false;
	
	public boolean failSafe;
	public Array<EntitySystem> visibleSystems = new Array<EntitySystem>();
	
	public LoadedCallback loadedCallback;
}
