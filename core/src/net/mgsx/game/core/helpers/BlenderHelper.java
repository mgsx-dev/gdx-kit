package net.mgsx.game.core.helpers;

import com.badlogic.gdx.Gdx;

public class BlenderHelper {

	public static float fov(float blenderFOV, float blenderRatio){
		float ratio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		return MathHelper.hFOVtovFOV(blenderFOV, blenderRatio / ratio); // TODO ? / *
	}
}
