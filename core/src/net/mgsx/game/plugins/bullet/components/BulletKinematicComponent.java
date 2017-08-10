package net.mgsx.game.plugins.bullet.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Matrix4;

public class BulletKinematicComponent implements Component
{
	
	public final static ComponentMapper<BulletKinematicComponent> components = ComponentMapper
			.getFor(BulletKinematicComponent.class);
	
	public transient Matrix4 transform;
}
