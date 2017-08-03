package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.utils.Array;

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
	public String [] achievements;
	
	/** current running quests, derived from achievements and game model */
	public transient Array<String> quests;
	
	/** persisted statistics */
	public double distanceWalk, distanceSwim, distanceFly, timeSleep, timePlay;
}
