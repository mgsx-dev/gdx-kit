package net.mgsx.game.core.helpers;

import com.badlogic.gdx.math.MathUtils;

public class MathHelper {

	// V = 2 * arctan( tan(H / 2) * aspectratio ) 
	// H = 2 * arctan( tan(V / 2) / aspectratio )
	// with aspectratio = x/y
	
	public static float vFOVtohFOV(float vFOV, float aspectRatio)
	{
		return (float)(2 * Math.atan(Math.tan(vFOV * MathUtils.degreesToRadians / 2) / aspectRatio) * MathUtils.radiansToDegrees);

	}
	public static float hFOVtovFOV(float hFOV, float aspectRatio)
	{
		return (float)(2 * Math.atan(Math.tan(hFOV * MathUtils.degreesToRadians / 2) * aspectRatio) * MathUtils.radiansToDegrees);

	}
	
}
