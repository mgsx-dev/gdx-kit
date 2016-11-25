package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.water-zone")
@EditableComponent(name="Water Zone")
public class WaterZone implements Component
{
	
	public final static ComponentMapper<WaterZone> components = ComponentMapper.getFor(WaterZone.class);
}
