package net.mgsx.game.examples.platformer.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.platformer.PlatformerGame;
import net.mgsx.game.examples.platformer.PlatformerWorkflow;

public class LevelScreen extends GameScreen
{
	private final PlatformerWorkflow game;
	
	public LevelScreen(PlatformerGame game, Engine engine) 
	{
		super(game.getAssets(), engine);
		this.game = game;
	}
	
	@Override
	public void render(float delta) {
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) game.abortGame();
		super.render(delta);
	}

}
