package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SpiderComponent implements Component
{
	public boolean init;
	
	public float life;
	public float time;
	
	public Rectangle zone = new Rectangle();
	
	public Vector2 target = new Vector2();
	
	public float speed = 1;
}
