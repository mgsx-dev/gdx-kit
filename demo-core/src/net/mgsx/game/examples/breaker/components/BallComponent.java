package net.mgsx.game.examples.breaker.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.physics.box2d.Body;

public class BallComponent implements Component
{
	
	public final static ComponentMapper<BallComponent> components = ComponentMapper.getFor(BallComponent.class);
	
	public Body body;
}
