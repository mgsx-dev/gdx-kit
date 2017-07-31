package net.mgsx.game.examples.openworld.ui;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.openworld.model.OpenWorldGame;
import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.SavedGame;

// TODO threading for all remote calls
// TODO don't recall service some times : just sync local lists !
public class SavedGameView extends Table
{
	private Engine engine;
	
	public SavedGameView(Skin skin, Engine engine) {
		super(skin);
		this.engine = engine;
		setBackground("default-window");
		buildUI();
	}

	private void buildUI() 
	{
		clearChildren();
		
		Array<SavedGame> games = GAPI.service.listGames();
		
		Table list = new Table(getSkin());
		list.setBackground("default-window");
		for(SavedGame game : games){
			createGameRow(list, game);
		}
		
		TextButton btSave = new TextButton("Save", getSkin());
		
		add(btSave).fill().row();
		add(list).fill().row();
		
		btSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				saveGame();
			}
		});
	}
	
	private void createGameRow(Table list, final SavedGame game){
		
		TextButton btDelete = new TextButton("Delete", getSkin());
		TextButton btLoad = new TextButton("Load", getSkin());
		
		list.add(game.name);
		list.add(btDelete);
		list.add(btLoad);
		list.row();
		
		btDelete.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				deleteGame(game);
			}
		});
		
		btLoad.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				loadGame(game);
			}
		});
	}
	
	private void saveGame() {
		
		// TODO get form engine
		
		SavedGame game = GAPI.service.createGame();
		
		game.name = "save-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		
		GAPI.service.saveGame(game, OpenWorldGame.save(engine));
		
		buildUI(); // TODO optimize by reduce remote API calls
	}

	private void loadGame(final SavedGame game) {
		InputStream stream = GAPI.service.loadGame(game);
		
		OpenWorldGame.load(engine, stream);
	}
	
	private void deleteGame(final SavedGame game) {
		GAPI.service.deleteGame(game);
		buildUI(); // TODO optimize by reduce remote API calls
	}

	
}
