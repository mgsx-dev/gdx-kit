package net.mgsx.game.core;

import com.badlogic.gdx.Game;

public class GameApplication extends Game
{
	private final GameScreen screen;
	
	public GameApplication(GameScreen screen) {
		super();
		this.screen = screen;
	}

	@Override
	public void create() {
		screen.registry = new GameRegistry();
		setScreen(screen);
	}
	
}
