package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.graphics.Color;

public class OpenWorldElement {

	// shape
	public float geo_x;
	public float geo_y;
	// material
	public Color color;
	// volume
	public float size;
	// rarity (-1 for fusion only objects)
	public float rarity;
	
	// TODO position ?
	
	// runtime seed (id for other generators)
	public long seed;
	
	/** bit wise boolean for wood, stone, metal, sand, water ... rates when destroyed 
	 * factor of size, eg. if only wood then it is made of size of wooks, if wood and stone
	 * then wood is made of size of woods / 2, same for stone.
	 * It's the history of the item
	 * */
	public long composition;
	
	
}
