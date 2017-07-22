package net.mgsx.game.services.gapi;

import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class GAPIServiceStub implements GAPIService {

	private void error(){
		throw new GdxRuntimeException("GAPIService no implementation available for the current platform.");
	}

	@Override
	public void init(String applicationName) {
		error();
	}
	
	@Override
	public void connect(String user) {
		error();
	}

	@Override
	public void unlockAchievement(String id) {
		error();
	}

	@Override
	public void incrementAchievement(String id, int steps) {
		error();
	}

	@Override
	public void submitScore(String id, long score) {
		error();
	}

	@Override
	public Array<Achievement> fetchAchievements() {
		error();
		return null;
	}

	@Override
	public Array<Leaderboard> fetchLeaderboards() {
		error();
		return null;
	}

	@Override
	public SavedGame createGame() {
		error();
		return null;
	}

	@Override
	public void saveGame(SavedGame game, InputStream data) {
		error();
	}

	@Override
	public void saveGame(SavedGame game, FileHandle data) {
		error();
	}

	@Override
	public Array<SavedGame> listGames() {
		error();
		return null;
	}

	@Override
	public InputStream loadGame(SavedGame game) {
		error();
		return null;
	}

	@Override
	public void deleteGame(SavedGame game) {
		error();
	}

}
