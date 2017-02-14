package net.mgsx.game.examples.platformer.screens;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;

import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.platformer.game.PlatformerGame;

public class LevelScreen extends GameScreen
{
	public StateMachine<LevelScreen, State<LevelScreen>> fsm;
	
	public final PlatformerGame game;
	
	public LevelScreen(PlatformerGame game, GameRegistry registry) 
	{
		super(game.getAssets(), registry);
		this.game = game;
	}
	
	@Override
	public void show() 
	{
		this.fsm = new DefaultStateMachine<LevelScreen, State<LevelScreen>>(this, LevelState.INIT);
		super.show();
	}
	
	@Override
	public void render(float delta) 
	{
		fsm.update();
		super.render(delta);
	}

}
