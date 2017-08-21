package net.mgsx.game.plugins.camera.model;

import com.badlogic.gdx.graphics.Camera;

/**
 * PointOfView model registered and injectable in any systems or tools.
 * 
 * It own the current camera which could be perspective or orthographic.
 * 
 * @author mgsx
 *
 */
public class POVModel {
	public Camera camera;
}
