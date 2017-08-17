package net.mgsx.game.examples.lsystem.model.l2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.examples.lsystem.model.Context;

public class Context2D implements Context<Context2D> {
	public Vector3 position = new Vector3();
	public Vector3 orientation = new Vector3(Vector3.X);
	public Vector3 up = new Vector3(Vector3.Y);
	public Vector3 scale = new Vector3(1,1,1);
	public Color color = new Color(Color.WHITE);
	public ShapeRenderer renderer;
	
	@Override
	public void clone(Context2D clone) {
		clone.position.set(position);
		clone.orientation.set(orientation);
		clone.up.set(up);
		clone.scale.set(scale);
		clone.color.set(color);
		clone.renderer = renderer;
	}
}
