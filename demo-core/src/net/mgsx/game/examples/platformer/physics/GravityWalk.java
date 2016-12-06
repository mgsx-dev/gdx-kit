package net.mgsx.game.examples.platformer.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;

@EditableComponent
public class GravityWalk implements Component
{
	
	public final static ComponentMapper<GravityWalk> components = ComponentMapper.getFor(GravityWalk.class);
	
	public final Vector2 groundNormal = new Vector2(0,1);

	@Editable public float force;
}
