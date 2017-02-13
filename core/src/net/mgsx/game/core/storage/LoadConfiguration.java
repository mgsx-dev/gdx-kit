package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GameRegistry;

public class LoadConfiguration {

	public AssetManager assets;
	public GameRegistry registry;
	public Engine engine;
	public boolean failSafe;
	public Array<EntitySystem> visibleSystems = new Array<EntitySystem>();
}
