package net.mgsx.game.examples.platformer.sensors;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("example.platformer.sugar-zone")
@EditableComponent
public class SugarZone implements Component {
	
	public final static ComponentMapper<SugarZone> components = ComponentMapper.getFor(SugarZone.class);
}
