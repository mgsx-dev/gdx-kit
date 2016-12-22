package net.mgsx.game.plugins.box2d.helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

public class RayCastResult{
	public Fixture fixture;
	public final Vector2 point = new Vector2();
	public final Vector2 normal = new Vector2();
	public float fraction;
	public boolean isValid(){
		return fixture != null;
	}
	public void reset() {
		fixture = null;
	}
}