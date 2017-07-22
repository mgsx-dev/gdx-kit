package net.mgsx.game.examples.openworld.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.SavedGame;

// TODO threading for all remote calls
// TODO don't recall service some times : just sync local lists !

public class SavedGameView extends Table
{

	public static class DummyGameData {
		public long seed = 0;
		public Vector3 position = new Vector3();
	}
	
	public SavedGameView(Skin skin) {
		super(skin);
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
		game.name = "save-" + DateFormat.getInstance().format(new Date());
		
		DummyGameData gameData = new DummyGameData();
		gameData.seed = 0xdeadbeef;
		gameData.position.set(3.2f, 6.5f, 8.1f);
		
		Json json = new Json();
		StringWriter writer = new StringWriter();
		json.toJson(gameData, writer);
		
		GAPI.service.saveGame(game, new ByteArrayInputStream(writer.toString().getBytes()));
		
		buildUI(); // TODO optimize by reduce remote API calls
	}

	private void loadGame(final SavedGame game) {
		InputStream stream = GAPI.service.loadGame(game);
		
		Json json = new Json();
		
		DummyGameData gameData = json.fromJson(DummyGameData.class, stream);
		
		System.out.println(gameData.seed);
		System.out.println(gameData.position);
		
		// TODO set to engine
	}
	
	private void deleteGame(final SavedGame game) {
		GAPI.service.deleteGame(game);
		buildUI(); // TODO optimize by reduce remote API calls
	}

	
}
