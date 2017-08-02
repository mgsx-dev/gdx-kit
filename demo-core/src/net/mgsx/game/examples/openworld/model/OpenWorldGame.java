package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.math.Vector3;

public class OpenWorldGame {

	public long seed = 0;
	public Vector3 position = new Vector3();
	public OpenWorldElement [] objects;
	public OpenWorldElement [] backpack;
	
	public OpenWorldPlayer player;
}
