package net.mgsx.kit.services.gapi;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.games.model.AchievementDefinition;
import com.google.api.services.games.model.AchievementIncrementResponse;
import com.google.api.services.games.model.AchievementUnlockResponse;
import com.google.api.services.games.model.Leaderboard;
import com.google.api.services.games.model.PlayerAchievement;
import com.google.api.services.games.model.PlayerScoreResponse;

import net.mgsx.game.services.gapi.Achievement;
import net.mgsx.game.services.gapi.GAPIService;
import net.mgsx.game.services.gapi.SavedGame;

/**
 * Desktop implementation (REST API in "Installed app" mode)
 * 
 * Note that Quests, events and  gifts is abandonned at march 2018,
 * see https://android-developers.googleblog.com/2017/04/focusing-our-google-play-games-services.html
 * 
 * @author mgsx
 *
 */
public class GAPIServiceDesktop implements GAPIService {

	// TODO identify saved game form cover or other files (metadata ?)
	// eg. GAPIGateway.drive.files().list().setSpaces("appDataFolder").setQ("name=" + name).execute().getFiles();
	// see https://developers.google.com/drive/v3/web/search-parameters

	
	private void apiError() {
		throw new GdxRuntimeException("GAPI remote error");
	}
	
	@Override
	public void init(String applicationName) {
		try {
			GAPIGateway.init(applicationName, Gdx.files.internal("client_secrets.json"));
		} catch (GeneralSecurityException e) {
			throw new GdxRuntimeException(e);
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}
	
	@Override
	public void connect(String user) {
		try {
			GAPIGateway.authorize(user);
		} catch (Exception e) {
			throw new GdxRuntimeException(e);
		}
	}

	
	@Override
	public SavedGame createGame() {
		return new SavedGameDrive();
	}

	@Override
	public void saveGame(SavedGame game, InputStream data) {
		// create temporary file (required by drive API)
		try {
			java.io.File file = java.io.File.createTempFile("games", "dat");
			new FileHandle(file).write(data, false);
			saveGame(game, file);
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public void saveGame(SavedGame game, FileHandle data) {
		// read file if needed
		java.io.File file = data.file();
		if(file == null){
			saveGame(game, data.read());
		}else{
			saveGame(game, file);
		}
	}
	
	private void saveGame(SavedGame game, java.io.File data) {
		try {
			
			SavedGameDrive g = (SavedGameDrive)game;
			
			// no type since it may be binary data
			FileContent mediaContent = new FileContent(null, data);
			
			// file exists then update it
			if(g.contentDriveId != null){
				
				// just update content, leave metadata intact.
				File file = GAPIGateway.drive.files().update(g.contentDriveId, null, mediaContent)
						.setFields("id")
						.execute();
				
				Gdx.app.log("GAPI", "File updated ID: " + file.getId());
			}
			// file doesn't exists then create it
			else{
				File fileMetadata = new File();
				fileMetadata.setName(game.name);
				
				// app folder is a reserved keyyword for current application private folder.
				fileMetadata.setParents(Collections.singletonList("appDataFolder"));
				
				File file = GAPIGateway.drive.files().create(fileMetadata, mediaContent)
						.setFields("id")
						.execute();
				
				g.contentDriveId = file.getId();
				
				Gdx.app.log("GAPI", "File created ID: " + file.getId());
			}
			
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}
	

	@Override
	public InputStream loadGame(SavedGame game) {
		try {
			
			SavedGameDrive g = (SavedGameDrive)game;
			
			return GAPIGateway.drive.files().get(g.contentDriveId).executeMediaAsInputStream();
			
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public void deleteGame(SavedGame game) {
		try {
			SavedGameDrive g = (SavedGameDrive)game;
			GAPIGateway.drive.files().delete(g.contentDriveId).execute();
			Gdx.app.log("GAPI", "File deleted ID: " + g.contentDriveId);
			g.contentDriveId = null;
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}
	
	@Override
	public Array<SavedGame> listGames() {
		
		Array<SavedGame> games = new Array<SavedGame>();
		
		try {
			FileList l = GAPIGateway.drive.files().list()
					.setSpaces("appDataFolder")
					.setFields("nextPageToken, files(id, name)")
					.setPageSize(10)
					.execute();
			
			for(File f : l.getFiles()){
				SavedGameDrive game = new SavedGameDrive();
				game.contentDriveId = f.getId();
				game.name = f.getName();
			}
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
		
		return games;
		
	}

	@Override
	public void unlockAchievement(String id) {
		try {
			AchievementUnlockResponse r = GAPIGateway.games.achievements().unlock(id).execute();
			if(r == null) apiError();
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public void incrementAchievement(String id, int steps) {
		try {
			AchievementIncrementResponse r = GAPIGateway.games.achievements().increment(id, steps).execute();
			if(r == null) apiError();
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public void submitScore(String id, long score) {
		try {
			PlayerScoreResponse r = GAPIGateway.games.scores().submit(id, score).execute();
			if(r == null) apiError();
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public Array<Achievement> fetchAchievements() {
		try{
			Array<Achievement> achievements = new Array<Achievement>();
			ObjectMap<String, PlayerAchievement> playerAchievements = new ObjectMap<String, PlayerAchievement>();
			for(PlayerAchievement a : GAPIGateway.games.achievements().list("me").execute().getItems()){
				playerAchievements.put(a.getId(), a);
			}
			
			for(AchievementDefinition def : GAPIGateway.games.achievementDefinitions().list().execute().getItems()){
				Achievement a = new Achievement();
				a.id = def.getId();
				a.name = def.getName();
				a.description = def.getDescription();
				a.revealedIconUrl = def.getRevealedIconUrl();
				a.unlockedIconUrl = def.getUnlockedIconUrl();
				a.type = def.getAchievementType();
				
				boolean hasSteps = a.isIncremental();
				
				a.totalSteps = hasSteps ? def.getTotalSteps().intValue() : 0;
				
				PlayerAchievement p = playerAchievements.get(def.getId());
				if(p != null){
					a.currentSteps = hasSteps ? p.getCurrentSteps().intValue() : 0;
					a.state = p.getAchievementState();
				}
				
				achievements.add(a);
			}
			
			return achievements;
			
		}catch(IOException e){
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public Array<net.mgsx.game.services.gapi.Leaderboard> fetchLeaderboards() {
		try{
			Array<net.mgsx.game.services.gapi.Leaderboard> leaderBoards = new Array<net.mgsx.game.services.gapi.Leaderboard>();
			for(Leaderboard def : GAPIGateway.games.leaderboards().list().execute().getItems()){
				net.mgsx.game.services.gapi.Leaderboard l = new net.mgsx.game.services.gapi.Leaderboard();
				l.id = def.getId();
				l.iconUrl = def.getIconUrl();
				l.name = def.getName();
				l.order = def.getOrder();
				leaderBoards.add(l);
				// TODO fetch player scores in another method
				// scores.put(a.getId(), GoogleAPI.games.scores().get("me", a.getId(), "ALL").execute().getItems());
			}
			return leaderBoards;
		}catch(IOException e){
			throw new GdxRuntimeException(e);
		}
	}

}
