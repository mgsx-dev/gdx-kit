package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@EditableComponent(autoClone=true)
@Storable("example.platformer.surrounding.cavern")
public class CavernSurrounding implements Component
{
	
	public final static ComponentMapper<CavernSurrounding> components = ComponentMapper.getFor(CavernSurrounding.class);
	
	@Editable public float density;
	
	public float distance;
}
