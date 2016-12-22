package net.mgsx.game.plugins.box2d.helper;

import com.badlogic.gdx.math.Vector2;

public class RayCast {

	public Vector2 start = new Vector2();
	public Vector2 end = new Vector2();
	
	public RayCast set(Vector2 start, Vector2 direction, float length){
		this.start.set(start);
		this.end.set(start).mulAdd(direction, length);
		return this;
	}

	public RayCast set(RayCast rayCast) {
		this.start.set(rayCast.start);
		this.end.set(rayCast.end);
		return this;
	}
}
