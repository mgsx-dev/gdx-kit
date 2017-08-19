package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * double float precision is used here because values could be huge
 * and delta computations are based on very small values.
 * This extra computation/storage is not significant due to volume of data :
 * a single entity and less than 100 values.
 * 
 * @author mgsx
 *
 */
public class OpenWorldPlayer {

	/** player current states */
	public double life, energy, oxygen;
	public long experience;
	
	/** derived from experience */
	public transient int level;
	
	/** derived from environement, equipement, experience */
	public transient double temperature, temperatureMin, temperatureMax, lifeMax, energyMax, oxygenMax;
	
	/** quest complete codes from model */
	public String [] achievements = new String[]{};
	
	/** current running quests, derived from achievements and game model */
	public transient Array<String> pendingQuests = new Array<String>();
	
	/** completed quests, derived from achievements at start and from model and events during the game */
	public transient Array<String> completedQuests = new Array<String>();
	
	/** acknowledge quests status, used during the game to wait player to see quest */
	public transient ObjectSet<String> acknowledgedQuests = new ObjectSet<String>();
	
	public String [] history = new String[]{};
	
	/** */
	public transient ObjectMap<String, ObjectMap<String, Integer>> actions = new ObjectMap<String, ObjectMap<String,Integer>>();
	
	/** persisted statistics */
	public double distanceWalk, distanceSwim, distanceFly, timeSleep, timePlay;
	
	public transient ObjectSet<String> knownSecrets = new ObjectSet<String>();
}
