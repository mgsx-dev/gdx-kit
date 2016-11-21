package net.mgsx.game.examples.platformer;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.screen.TransitionListener;
import net.mgsx.game.core.screen.Transitions;
import net.mgsx.game.examples.platformer.screens.GameLoadingScreen;
import net.mgsx.game.examples.platformer.screens.GameMenuScreen;
import net.mgsx.game.examples.platformer.screens.LevelLoadingScreen;
import net.mgsx.game.examples.platformer.screens.LevelScreen;

/**
 * Platformer Game Application (official entry point)
 * 
 * This classe is responsible of platformer screen workflow : 
 * - loading (simple splash screen)
 * - title screen (engine with HUD menu)
 * - level loading screen (interactive loading screen)
 * - level screen (in game screen with engine)
 * 
 * @author mgsx
 *
 */
public class PlatformerGame extends GameApplication implements PlatformerWorkflow
{
	// all cached screens
	private GameLoadingScreen gameLoadingScreen;
	private GameMenuScreen gameMenuScreen;
	private LevelLoadingScreen levelLoadingScreen;
	private LevelScreen levelScreen;
	
	private boolean gameStarted = false;
	
	@Override
	public void create() 
	{
		super.create();
		
		// create game loading screen, force asset loading and show it
		gameLoadingScreen = new GameLoadingScreen(this);
		assets.finishLoading();
		setScreen(gameLoadingScreen);
		
		// set this screen as recovery screen as well.
		setDefaultLoadingScreen(gameLoadingScreen);
		
		// create all cached screens (menu)
		gameMenuScreen = new GameMenuScreen(this);
		
		// create screen sequence.
		addTransition(Transitions.fade(Transitions.empty(Color.BLACK), 1));
		addTransition(Transitions.fade(gameMenuScreen, 1));
	}
	
	@Override
	public AssetManager assets(){
		return assets;
	}
	
	@Override
	public void startGame()
	{
		if(gameStarted) return;
		gameStarted = true;
		
		// create start game sequence :
		// first create both level loading and level screens.
		// then fadeout current screen
		// prepare loading
		// fadein level loading screen
		// when all is loaded, fadeout level loading screen
		// then fadein level screen
		// finally set input processor
		
		// TODO just create screens
		// load level loading screen
		if(levelLoadingScreen == null)
		{
			levelLoadingScreen = new LevelLoadingScreen(this, new PooledEngine());
			levelLoadingScreen.registry = new GameRegistry();
			levelLoadingScreen.registry.registerPlugin(new PlatformerPlugin());
			levelLoadingScreen.registry.init(levelLoadingScreen);
		}
		levelLoadingScreen.load(Gdx.files.internal("levels/level-load.json"));
		
		// create the game screen (not cached)
		levelScreen = new LevelScreen(this, new PooledEngine());
		
		levelScreen.registry = new GameRegistry();
		levelScreen.registry.registerPlugin(new PlatformerPlugin());
		levelScreen.registry.init(levelScreen);
		levelScreen.load(Gdx.files.internal("levels/level1.json"));
		
		setTransition(Transitions.fade(Transitions.empty(Color.BLACK), 1.f));
		addTransition(Transitions.fade(levelLoadingScreen, 1.f));
		addTransition(Transitions.fade(Transitions.empty(Color.WHITE), 1.f));
		addTransition(Transitions.fade(levelScreen, 1.f));
		
	}
	
	@Override
	public void abortGame()
	{
		if(!gameStarted) return;
		gameStarted = false;
		
		// transition :
		// fadeout level screen
		// dispose it
		// fadein menu screen
		setTransition(Transitions.fade(Transitions.empty(Color.BLACK), 1.f, new TransitionListener(){
			@Override
			public void end() {
				levelScreen.dispose();
				levelScreen = null;
			}
		}));
		
		addTransition(Transitions.fade(gameMenuScreen, 1.f));
		
		
		
	}

}
