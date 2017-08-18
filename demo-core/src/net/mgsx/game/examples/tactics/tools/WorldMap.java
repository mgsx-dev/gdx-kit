package net.mgsx.game.examples.tactics.tools;

import com.badlogic.gdx.utils.Array;

public class WorldMap 
{
	public long seed;
	public int originX, originY;

	public int heroX, heroY;
	public float time = 0;
	
	public Array<WorldCell> discovered = new Array<WorldCell>();
	
}
