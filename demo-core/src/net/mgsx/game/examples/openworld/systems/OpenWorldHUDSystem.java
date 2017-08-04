package net.mgsx.game.examples.openworld.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import net.mgsx.game.core.PostInitializationListener;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.model.OpenWorldRuntimeSettings;
import net.mgsx.game.examples.openworld.ui.ConnectionView;
import net.mgsx.game.examples.openworld.ui.OpenWorldHUD;
import net.mgsx.game.examples.openworld.ui.SavedGameView;
import net.mgsx.game.examples.openworld.ui.ScenarioView;
import net.mgsx.game.plugins.core.systems.HUDSystem;
import net.mgsx.game.services.gapi.GAPI;

@EditableSystem
public class OpenWorldHUDSystem extends HUDSystem implements PostInitializationListener
{
	@Asset("openworld/game-skin.json")
	public Skin skin;
	
	private Table root;
	private Table view;
	
	@Inject OpenWorldGameSystem gameSystem;
	@Inject OpenWorldMapSystem mapSystem;
	
	public transient OpenWorldHUD hudMain;
	
	@Override
	public void onPostInitialization() {
		
		hudMain = new OpenWorldHUD(skin, getEngine());
		hudMain.setFillParent(true);
		hudMain.setVisible(false);
		getStage().addActor(hudMain);
		
		root = new Table(skin);
		root.setFillParent(true);
		getStage().addActor(root);
	}
	
	@Override
	public void update(float deltaTime) {
		// XXX workaround for click bug listener but could be the desktop version of android properties button ?
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
			if(hudMain.isVisible()){
				hudMain.setVisible(false);
				mapSystem.setProcessing(false);
			}else{
				hudMain.setVisible(true);
				mapSystem.setProcessing(true);
			}
		}
		
		super.update(deltaTime);
	}
	
	@Editable
	public void hide() 
	{
		root.clearChildren();
	}

	@Editable
	public void showConnection() 
	{
		root.clearChildren();
		view = new ConnectionView(skin);
		root.add(view);
	}

	@Editable
	public void showSavedGames() 
	{
		root.clearChildren();
		view = new SavedGameView(skin, getEngine());
		root.add(view);
	}
	
	@Editable
	public void testScenario() {
		
		root.clearChildren();
		view = new ScenarioView(skin);
		root.add(view);
		
	}
	@Editable
	public void testCompleteAch() {
		
		GAPI.service.unlockAchievement("CgkI9ouoooEcEAIQAw");
		
	}
	@Editable
	public void testIncAch() {
		
		GAPI.service.incrementAchievement("CgkI9ouoooEcEAIQAg", 1);
		
	}
	@Editable
	public void testUpdateLead() {
		
		GAPI.service.submitScore("CgkI9ouoooEcEAIQBQ", 500L);
	}
	@Editable
	public void testNewAppInitalize() {
		
		OpenWorldRuntimeSettings.gsc.setListener(new IGameServiceListener() {
			
			@Override
			public void gsGameStateLoaded(byte[] gameState) {
				Gdx.app.log("GPGS", "game loaded !");
			}
			
			@Override
			public void gsErrorMsg(GsErrorType et, String msg) {
				Gdx.app.log("GPGS", "error : " + msg);
			}
			
			@Override
			public void gsDisconnected() {
				Gdx.app.log("GPGS", "disconnected !");
			}
			
			@Override
			public void gsConnected() {
				Gdx.app.log("GPGS", "connected !");
			}
		});
		OpenWorldRuntimeSettings.gsc.connect(false);
	}
	@Editable
	public void testNewAppInc() {
		OpenWorldRuntimeSettings.gsc.incrementAchievement("CgkI9ouoooEcEAIQAg", 1);
	}
	@Editable
	public void testNewAppPlayerInfo() {
		Gdx.app.log("GPGS", "service id : " + OpenWorldRuntimeSettings.gsc.getGameServiceId());
		Gdx.app.log("GPGS", "player name : " + OpenWorldRuntimeSettings.gsc.getPlayerDisplayName());
		Gdx.app.log("GPGS", "connected ? : " + OpenWorldRuntimeSettings.gsc.isConnected());
	}
	@Editable
	public void testNewAppLogIn() {
		OpenWorldRuntimeSettings.gsc.connect(false);
	}
	@Editable
	public void testNewAppLogOut() {
		OpenWorldRuntimeSettings.gsc.logOff();
	}
	@Editable
	public void testNewAppShowAll() {
		try {
			OpenWorldRuntimeSettings.gsc.showAchievements();
		} catch (GameServiceException e) {
			Gdx.app.error("GPGS",  "error show achievements ...", e);
		}
	}
}
