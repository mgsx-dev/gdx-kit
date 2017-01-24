package net.mgsx.game.examples.war.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.war.model.Zone;

@Storable(value="war.zone", auto=true)
@EditableComponent(autoClone=true)
public class ZoneComponent implements Component
{
	
	public final static ComponentMapper<ZoneComponent> components = ComponentMapper.getFor(ZoneComponent.class);
	
	public Zone zone;
	
}
