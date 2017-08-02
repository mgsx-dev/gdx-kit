package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class OpenWorldElement {

	public static enum GeometryType{
		BOX, SPHERE
	}
	
	public String type;
	
	// shape
	public float geo_x;
	public float geo_y;
	// material
	public Color color;
	// volume
	public float size;
	// rarity (-1 for fusion only objects)
	public float rarity;
	
	public boolean dynamic = false;
	
	// runtime seed (id for other generators)
	public long seed;
	
	/** bit wise boolean for wood, stone, metal, sand, water ... rates when destroyed 
	 * factor of size, eg. if only wood then it is made of size of wooks, if wood and stone
	 * then wood is made of size of woods / 2, same for stone.
	 * It's the history of the item
	 * */
	public long composition;
	
	public Vector3 position = new Vector3();
	public Quaternion rotation = new Quaternion();

	/** this is the reference */
	public String name;
	
}
