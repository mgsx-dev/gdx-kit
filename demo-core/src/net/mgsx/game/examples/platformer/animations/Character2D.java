package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.annotations.Storable;

@Storable("demo.platformer.character")
@EditableComponent(autoClone=true)
public class Character2D implements Component
{
	
	public final static ComponentMapper<Character2D> components = ComponentMapper.getFor(Character2D.class);
	
	/** character orientation. 2D character maybe be rotated in 2D plan
	 * but it can be mirrored in some way to reflect its direction. */
	@Editable public boolean rightToLeft;
	
	/**
	 * degree per seconds
	 */
	@Editable(type=EnumType.UNIT)
	public float angularSpeed = 10f; 

	/**
	 * fake 3D rotation with a bias
	 */
	@Editable(type=EnumType.UNIT)
	public float angleRange = 75f; // angle

	
	public float angle;

	@Editable
	public boolean facing = false;
}
