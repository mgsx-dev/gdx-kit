package net.mgsx.game.core.annotations;

import com.badlogic.gdx.Input;

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
	UNIT
}
