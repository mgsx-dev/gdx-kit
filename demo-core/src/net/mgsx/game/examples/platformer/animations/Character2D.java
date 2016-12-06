package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Component;

public class Character2D implements Component
{
	/** character orientation. 2D character maybe be rotated in 2D plan
	 * but it can be mirrored in some way to reflect its direction. */
	public boolean rightToLeft;
}
