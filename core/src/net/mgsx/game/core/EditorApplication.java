package net.mgsx.game.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.plugins.Plugin;

public class EditorApplication extends Game
{
	final private EditorConfiguration config;
	
	public EditorApplication(EditorConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public void create() 
	{
		for(Plugin plugin : config.plugins){
			config.registry.registerPlugin(plugin);
		}
		
		GameScreen screen = new GameScreen();

		screen.registry = config.registry;
		
		config.registry.init(screen);
		
		EditorScreen editorScreen = new EditorScreen(config, screen);
		
		if(config.path != null && config.root != null) {
			screen.load(Gdx.files.absolute(config.root).child(config.path));
		}
		
		setScreen(editorScreen);
	}

}
