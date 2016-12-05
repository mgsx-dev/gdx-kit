package net.mgsx.game.examples.platformer;

import com.badlogic.gdx.assets.AssetManager;

public interface PlatformerWorkflow {

	void abortGame();

	void startGame();

	AssetManager assets();
}
