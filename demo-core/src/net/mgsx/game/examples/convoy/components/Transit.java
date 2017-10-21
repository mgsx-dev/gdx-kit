package net.mgsx.game.examples.convoy.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class Transit implements Component {
	
	public final static ComponentMapper<Transit> components = ComponentMapper.getFor(Transit.class);
	
	public Entity target;
	
	public Vector2 origin = new Vector2();
	
	public float speed;
	
	public float t;
}
