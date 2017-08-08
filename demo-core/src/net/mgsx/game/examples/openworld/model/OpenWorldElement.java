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
	
	// TODO shouldn't be stored : based on item definition
	public boolean dynamic = false;

	/** in Kg/m³ some examples could be found here :
	 * https://en.wikipedia.org/wiki/Density#Various_materials*/
	public transient float density;

	/** original damping for object */
	public transient float damping;

	/** optional model path for procedural models */
	public transient String modelPath;

	/** for moving elements ability in environment */
	public transient boolean landAbility, airAbility, waterAbility;
}
