package net.mgsx.game.examples.platformer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;

import net.mgsx.game.core.screen.TransitionListener;
import net.mgsx.game.core.screen.Transitions;
import net.mgsx.game.examples.platformer.screens.GameLoadingScreen;
import net.mgsx.game.examples.platformer.screens.GameMenuScreen;

/**
 * Game state definition.
 * 
 * @author mgsx
 *
 */
public enum PlatformerGameState implements State<PlatformerGame>
{
	INIT(){
		@Override
		public void update(PlatformerGame entity) {
			
			// create game loading screen, force asset loading and show it
			entity.gameLoadingScreen = new GameLoadingScreen(entity.getAssets());
			entity.getAssets().finishLoading();
			
			// set this screen as recovery screen as well.
			entity.setDefaultLoadingScreen(entity.gameLoadingScreen);
			
			// create all cached screens (menu, credits..)
			entity.gameMenuScreen = new GameMenuScreen(entity);
			
			entity.fsm.changeState(LOADING);
		}
	},
	LOADING(){
		@Override
		public void enter(PlatformerGame entity) {
			entity.setScreen(entity.gameLoadingScreen);
		}
		@Override
		public void update(PlatformerGame entity) {
			if(entity.getAssets().update()){
				entity.fsm.changeState(MENU_SCREEN);
			}
		}
		@Override
		public void exit(PlatformerGame entity) {
			entity.addTransition(Transitions.fade(Transitions.empty(Color.BLACK), 1));
		}
	},
	MENU_SCREEN(){
		
		@Override
		public void enter(PlatformerGame entity) {
			entity.addTransition(Transitions.fade(entity.gameMenuScreen, 1));
		}
		
		@Override
		public void startGame(PlatformerGame entity) {
			entity.fsm.changeState(LEVEL_SCREEN);
		}
		@Override
		public void showCredits(PlatformerGame entity) {
			entity.fsm.changeState(CREDITS_SCREEN);
		}
		
	},
	LEVEL_SCREEN(){
		@Override
		public void enter(PlatformerGame entity) {
			entity.createLevelScreen();
			
			entity.setTransition(Transitions.fade(Transitions.empty(Color.BLACK), 1.f));
			entity.addTransition(Transitions.fade(entity.levelLoadingScreen, 1.f));
			entity.addTransition(Transitions.fade(Transitions.empty(Color.WHITE), 1.f));
			entity.addTransition(Transitions.fade(entity.levelScreen, 1.f));
		}
		@Override
		public void update(PlatformerGame entity) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
				entity.fsm.changeState(MENU_SCREEN);
			}
		}
		
		@Override
		public void exit(PlatformerGame entity) {
			entity.setTransition(Transitions.fade(Transitions.empty(Color.BLACK), 1.f));
		}
		
		@Override
		public void abortGame(final PlatformerGame entity) {
			
			// transition :
			// fadeout level screen
			// dispose it
			// fadein menu screen
			entity.setTransition(Transitions.fade(Transitions.empty(Color.BLACK), 1.f, new TransitionListener(){
				@Override
				public void end() {
					entity.levelScreen.dispose();
					entity.levelScreen = null;
				}
			}));
			
			entity.fsm.changeState(MENU_SCREEN);

		}
		
	},
	CREDITS_SCREEN(){
		@Override
		public void back(PlatformerGame entity) {
			entity.fsm.changeState(MENU_SCREEN);
		}
	},
	
	GLOBAL(){
		@Override
		public void update(PlatformerGame entity) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
				entity.fsm.changeState(EXIT);
			}
		}
	},
	EXIT(){
		@Override
		public void enter(PlatformerGame entity) {
			Gdx.app.exit();
		}
	}
	;

	public void startGame(PlatformerGame entity){}
	public void abortGame(PlatformerGame entity){}
	
	public void showCredits(PlatformerGame entity){}
	public void back(PlatformerGame entity){}

	@Override
	public void enter(PlatformerGame entity) { }

	@Override
	public void update(PlatformerGame entity) { }

	@Override
	public void exit(PlatformerGame entity) { }

	@Override
	public boolean onMessage(PlatformerGame entity, Telegram telegram) {
		return false;
	}


}
