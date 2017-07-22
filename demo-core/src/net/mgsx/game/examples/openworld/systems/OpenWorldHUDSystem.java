package net.mgsx.game.examples.openworld.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.openworld.ui.ConnectionView;
import net.mgsx.game.plugins.core.systems.HUDSystem;
import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.SavedGame;

@EditableSystem
public class OpenWorldHUDSystem extends HUDSystem
{
	@Asset("skins/game-skin.json")
	public Skin skin;
	
	private Table root;
	private Table view;
	private boolean hidden = true;
	
	@Override
	public void update(float deltaTime) {
		// XXX init GUI in update because of dependency injection
		if(root == null){
			root = new Table(skin);
			root.setFillParent(true);
			buildUI();
		}
		super.update(deltaTime);
	}
	
	@Editable
	public void toggle() 
	{
		if(hidden){
			getStage().addActor(root);
			hidden = false;
		}else{
			root.remove();
			hidden = true;
		}
	}

	private void buildUI() 
	{
		view = new ConnectionView(skin);
		
		root.clearChildren();
		root.add(view);
	}

	@Editable
	public void testSaveGame() {
		
		SavedGame game = GAPI.service.createGame();
		game.name = "slot01.json";
		GAPI.service.saveGame(game, Gdx.files.local("kit-autosave.json"));
	}
	
	@Editable
	public void testLoadGames() {
		
		GAPI.service.listGames();
		
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
	

}
