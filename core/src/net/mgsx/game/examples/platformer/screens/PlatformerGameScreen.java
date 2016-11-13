package net.mgsx.game.examples.platformer.screens;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.storage.Storage;

public class PlatformerGameScreen extends GameScreen
{
	final private FileHandle levelFile;
	
	
	public PlatformerGameScreen(FileHandle levelFile) {
		super();
		this.levelFile = levelFile;
	}


	@Override
	public void show() 
	{
		super.show();
		
		if(levelFile != null) Storage.load(entityEngine, levelFile, assets, registry.serializers);
	}
}
