package net.mgsx.core.plugins;

import com.badlogic.gdx.math.Vector2;

public interface Pickable 
{
	public boolean hit(Vector2 screenCoordinates);
}
