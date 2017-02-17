package net.mgsx.game.examples.platformer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.examples.platformer.PlatformerPlugin;
import net.mgsx.game.examples.platformer.screens.GameLoadingScreen;
import net.mgsx.game.examples.platformer.screens.GameMenuScreen;
import net.mgsx.game.examples.platformer.screens.LevelLoadingScreen;
import net.mgsx.game.examples.platformer.screens.LevelScreen;

/**
 * Platformer Game Application (entry point)
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
// TODO it's over complicated : need to abstract screen lifecycle (Application Scope, Recycled, Not cached at all)
public class PlatformerGame extends GameApplication
{
	protected StateMachine<PlatformerGame, PlatformerGameState> fsm;
	
	// all cached screens
	protected GameLoadingScreen gameLoadingScreen;
	protected GameMenuScreen gameMenuScreen;
	protected LevelLoadingScreen levelLoadingScreen;
	protected LevelScreen levelScreen;
	
	@Override
	public void create() 
	{
		super.create();
		
		fsm = new DefaultStateMachine<PlatformerGame, PlatformerGameState>(this, PlatformerGameState.INIT, PlatformerGameState.GLOBAL);
	}
	
	@Override
	public void render() {
		fsm.update();
		super.render();
	}
	
	protected void createLevelScreen()
	{
		// create start game sequence :
		// first create both level loading and level screens.
		// then fadeout current screen
		// prepare loading
		// fadein level loading screen
		// when all is loaded, fadeout level loading screen
		// then fadein level screen
		// finally set input processor
		
		GameRegistry registry = new GameRegistry();
		registry.registerPlugin(new PlatformerPlugin());
		
		// load level loading screen
		if(levelLoadingScreen == null)
		{
			levelLoadingScreen = new LevelLoadingScreen(assets, registry);
		}
		levelLoadingScreen.load(Gdx.files.internal("levels/level-load.json"));
		assets.finishLoading(); // force finish loading assets for level loading screeen.
		
		// create the game screen (not cached)
		levelScreen = new LevelScreen(this, registry);
		levelScreen.load(Gdx.files.internal("levels/level1.json"));
	}
	
	public PlatformerGameState state(){
		return fsm.getCurrentState();
	}


}
