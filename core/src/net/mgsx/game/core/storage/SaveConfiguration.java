package net.mgsx.game.core.storage;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;

import net.mgsx.game.core.GameRegistry;

public class SaveConfiguration {
	public AssetManager assets;
	public Engine engine;
	public GameRegistry registry;
	public boolean pretty;
	public boolean stripPaths;
}
