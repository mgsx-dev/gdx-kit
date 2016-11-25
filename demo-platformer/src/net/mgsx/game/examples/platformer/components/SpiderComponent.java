package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.spider")
@EditableComponent(name="Spider Logic")
public class SpiderComponent implements Component, Poolable
{
	public boolean init;
	
	public float life;
	public float time;
	
	public Rectangle zone = new Rectangle();
	
	public Vector2 target = new Vector2();
	
	public float speed = 1;

	@Override
	public void reset() {
		init = false;
	}
}
