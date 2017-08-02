package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class OpenWorldElement {

	public static enum GeometryType{
		BOX, SPHERE
	}
	
	/** this is the reference */
	public String type;
	
	/** the item seed */
	public long seed;
	
	/** item matrix */
	public Vector3 position = new Vector3();
	public Quaternion rotation = new Quaternion();
	
	// procedural properties
	public transient GeometryType shape;
	public transient float geo_x;
	public transient float geo_y;
	// material
	public transient Color color;
	// volume
	public transient float size;
	
	// TODO not all are dynamic ... store or not ?
	public boolean dynamic = false;
}
