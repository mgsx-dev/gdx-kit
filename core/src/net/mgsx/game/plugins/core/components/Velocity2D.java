package net.mgsx.game.plugins.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

/**
 * Control velocity
 * 
 * @author mgsx
 *
 */
@Storable("2d.velocity")
@EditableComponent
public class Velocity2D implements Component
{
	public final static ComponentMapper<Velocity2D> components = ComponentMapper.getFor(Velocity2D.class);
	
	@Editable public Vector2 velocity = new Vector2();
}
