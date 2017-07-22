package net.mgsx.game.examples.openworld.systems;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.openworld.ui.ConnectionView;
import net.mgsx.game.examples.openworld.ui.SavedGameView;
import net.mgsx.game.plugins.core.systems.HUDSystem;
import net.mgsx.game.services.gapi.GAPI;

@EditableSystem
public class OpenWorldHUDSystem extends HUDSystem
{
	@Asset("skins/game-skin.json")
	public Skin skin;
	
	private Table root;
	private Table view;
	
	@Override
	public void update(float deltaTime) {
		// XXX init GUI in update because of dependency injection
		if(root == null){
			root = new Table(skin);
			root.setFillParent(true);
			getStage().addActor(root);
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
		view = new SavedGameView(skin);
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
}
