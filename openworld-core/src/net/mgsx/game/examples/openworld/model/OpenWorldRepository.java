package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.services.gapi.Achievement;
import net.mgsx.game.services.gapi.Leaderboard;

/**
 * Class responsible for game data handling which includes :
 * <ul>
 * <li>local storage</li>
 * <li>remote storage</li>
 * <li>google play service achievements / leaderboards</li>
 * </ul>
 * 
 * @author mgsx
 *
 */
public class OpenWorldRepository {

	public static Array<Achievement> achievements;
	public static Array<Leaderboard> leaderboards;
	
}
