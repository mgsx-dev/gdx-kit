package net.mgsx.game.examples.procedural.model.l3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.examples.procedural.model.Context;

public class Context3D implements Context<Context3D> {
	public Vector3 position = new Vector3();
	public Vector3 orientation = new Vector3();
	public Vector3 up = new Vector3();
	public Vector3 scale = new Vector3();
	public Color color = new Color();
	
	@Override
	public void clone(Context3D clone) {
		clone.position.set(position);
		clone.orientation.set(orientation);
		clone.up.set(up);
		clone.scale.set(scale);
		clone.color.set(color);
	}
}
