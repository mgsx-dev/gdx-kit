package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.utils.Array;

public class OpenWorldPlayer {

	/** player current states */
	public float life, energy, oxygen, experience;
	
	/** derived from experience */
	public transient int level;
	
	/** derived from environement, equipement, experience */
	public transient float temperature, temperatureMin, temperatureMax, lifeMax, energyMax, oxygenMax;
	
	/** quest complete codes from model */
	public String [] achievements;
	
	/** current running quests, derived from achievements and game model */
	public transient Array<String> quests;
	
	/** persisted statistics */
	public float distanceWalk, distanceSwim, distanceFly, timeSleep, timePlay;
}
