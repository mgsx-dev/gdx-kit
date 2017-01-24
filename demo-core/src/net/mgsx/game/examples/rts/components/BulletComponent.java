package net.mgsx.game.examples.rts.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BulletComponent implements Component, Poolable {
	
	public final static ComponentMapper<BulletComponent> components = ComponentMapper.getFor(BulletComponent.class);
	
	public Vector2 origin = new Vector2();
	public Vector2 direction = new Vector2();
	public float distance;
	public float speed;
	
	public Vector2 position = new Vector2();
	public float time;

	public Color color = new Color(Color.WHITE);
	@Override
	public void reset() {
		time = 0;
	}
}
