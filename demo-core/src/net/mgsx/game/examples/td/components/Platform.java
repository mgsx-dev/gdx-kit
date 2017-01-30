package net.mgsx.game.examples.td.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("td.platform")
@EditableComponent(autoClone=true)
public class Platform implements Component
{
	
	public final static ComponentMapper<Platform> components = ComponentMapper.getFor(Platform.class);
}
