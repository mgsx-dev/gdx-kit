package net.mgsx.game.services.gapi;

import java.io.InputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Local game service wrapper.
 * 
 * Wrap another service and synchronize saved games.
 * 
 * @author mgsx
 *
 */
public class LocalGameStorage implements GAPIService
{

	public static class LocalSavedGame extends SavedGame {}

	private FileHandle savedGameFolder;
	private GAPIService service;
	private boolean syncWithCloud;
	
	public LocalGameStorage(GAPIService service, boolean syncWithCloud) {
		this.service = service;
		this.syncWithCloud = syncWithCloud;
	}

	@Override
	public void init(String applicationName) {
		savedGameFolder = Gdx.files.external("." + applicationName);
		service.init(applicationName);
	}

	@Override
	public void connect(String user) {
		service.connect(user);
		if(syncWithCloud){
			// fetch remote games in order to compare with local.
			Array<SavedGame> remoteGames = service.listGames();
			ObjectMap<String, SavedGame> mapRemoteGames = new ObjectMap<String, SavedGame>();
			for(SavedGame cloudGame : remoteGames){
				mapRemoteGames.put(cloudGame.name, cloudGame);
			}
			// sync from local to cloud
			for(FileHandle file : savedGameFolder.list()){
				String name = file.name();
				SavedGame remoteGame = mapRemoteGames.get(name);
				if(remoteGame == null){
					remoteGame = service.createGame();
					remoteGame.name = name;
					service.saveGame(remoteGame, file);
				}
			}
			// sync from cloud to local
			for(SavedGame cloudGame : remoteGames){
				FileHandle localFile = savedGameFolder.child(cloudGame.name);
				if(!localFile.exists()){
					localFile.write(service.loadGame(cloudGame), false);
				}
			}
		}
	}

	@Override
	public SavedGame createGame() {
		if(syncWithCloud) return service.createGame();
		return new LocalSavedGame();
	}

	@Override
	public void saveGame(SavedGame game, InputStream data) {
		savedGameFolder.child(game.name).write(data, false);
		if(syncWithCloud) service.saveGame(game, data);
	}

	@Override
	public void saveGame(SavedGame game, FileHandle data) {
		saveGame(game, data.read());
	}

	@Override
	public Array<SavedGame> listGames() {
		if(syncWithCloud) return service.listGames();
		Array<SavedGame> games = new Array<SavedGame>();
		for(FileHandle file : savedGameFolder.list()){
			// TODO fileter
			LocalSavedGame game = new LocalSavedGame();
			game.name = file.name();
			games.add(game);
		}
		return games;
	}

	@Override
	public InputStream loadGame(SavedGame game) {
		if(syncWithCloud) return loadGame(game);
		return savedGameFolder.child(game.name).read();
	}

	@Override
	public void deleteGame(SavedGame game) {
		savedGameFolder.child(game.name).delete();
		if(syncWithCloud) service.deleteGame(game);
	}

	@Override
	public void unlockAchievement(String id) {
		service.unlockAchievement(id);
	}

	@Override
	public void incrementAchievement(String id, int steps) {
		service.incrementAchievement(id, steps);
	}

	@Override
	public void submitScore(String id, long score) {
		service.submitScore(id, score);
	}

	@Override
	public Array<Achievement> fetchAchievements() {
		return service.fetchAchievements();
	}

	@Override
	public Array<Leaderboard> fetchLeaderboards() {
		return service.fetchLeaderboards();
	}

}
