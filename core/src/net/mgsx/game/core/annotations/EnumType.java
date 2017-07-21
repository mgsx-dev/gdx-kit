package net.mgsx.game.core.annotations;

public enum EnumType {
	
	/** default : handle some predefnied types : 
	 * String, float, integer, Vector2, Vector3, Quaternion.
	 * */
	AUTO, 
	
	/** integer among {@link Input.Keys} values */
	GDX_KEYS, 
	
	
	/** type is considered as bits (work with int, short, byte, long) */
	BITS,
	
	/** type considered as unit value : range from 0 to 1 for floats,
	 * unit vector for Vector2 and Vector3.
	 *  */
	UNIT,
	
	BLEND_MODE, 
	
	/**
	 * typically used as a seed : value can be generated manually.
	 * supported types : 
	 * int and long : 0 (inclusive) to max positive (inclusive)
	 * float and double : 0 (inclusive) to 1 (exclusive)
	 */
	RANDOM, 
	
	/**
	 * value is formatted as human readable bytes value.
	 * Large values are postfixed with Ko, Mo, Go...
	 */
	BYTES
}
