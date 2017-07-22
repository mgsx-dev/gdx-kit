package net.mgsx.game.services.gapi;

import java.io.InputStream;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Google API service interface.
 * 
 * Note : all methods might throw {@link com.badlogic.gdx.utils.GdxRuntimeException} for
 * underlying {@link java.io.IOException} or any error. Caller is responsible to catch these
 * errors and display a user friendly message.
 * 
 * @author mgsx
 *
 */
public interface GAPIService {

	/**
	 * Initialize service. Must be called before any other methods.
	 * @param applicationName the application name registered in the google console.
	 */
	public void init(String applicationName);
	
	public void connect(String user);

	/**
	 * Create a new game to save. Caller can fill metadata and then call
	 * {@link #saveGame(SavedGame, byte[])} or {@link #saveGame(SavedGame, FileHandle)}
	 * to commit it.
	 * @return
	 */
	public SavedGame createGame();
	
	public void saveGame(SavedGame game, InputStream data);

	public void saveGame(SavedGame game, FileHandle data);

	public Array<SavedGame> listGames();

	/**
	 * @param game
	 * @return the stream. caller is responsible to close the stream.
	 */
	public InputStream loadGame(SavedGame game);

	public void deleteGame(SavedGame game);
	
	public void unlockAchievement(String id);

	public void incrementAchievement(String id, int steps);

	public void submitScore(String id, long score);

	public Array<Achievement> fetchAchievements();

	public Array<Leaderboard> fetchLeaderboards();
	
}
