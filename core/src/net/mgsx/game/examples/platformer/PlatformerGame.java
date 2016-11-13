package net.mgsx.game.examples.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.GameScreen;

public class PlatformerGame extends Game
{

	@Override
	public void create() 
	{
		// For now it's just a single screen game, menu, intro and transitions
		// will come later.
		// we boot up on level 1.
		
		GameScreen screen = new GameScreen();
		screen.registry = new GameRegistry();
		screen.registry.registerPlugin(new PlatformerPlugin());
		screen.registry.init(screen);
		
		screen.load(Gdx.files.internal("levels/level1.json"));
		
		setScreen(screen);
	}

}
