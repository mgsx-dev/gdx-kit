package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("demo.platformer.character")
@EditableComponent(autoClone=true)
public class Character2D implements Component
{
	
	public final static ComponentMapper<Character2D> components = ComponentMapper.getFor(Character2D.class);
	
	/** character orientation. 2D character maybe be rotated in 2D plan
	 * but it can be mirrored in some way to reflect its direction. */
	@Editable public boolean rightToLeft;
}
