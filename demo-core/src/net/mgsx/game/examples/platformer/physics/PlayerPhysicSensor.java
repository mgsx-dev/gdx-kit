package net.mgsx.game.examples.platformer.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;

@Storable("cake.physics.playerSensor")
@EditableComponent
public class PlayerPhysicSensor implements Component
{
	
	public final static ComponentMapper<PlayerPhysicSensor> components = ComponentMapper
			.getFor(PlayerPhysicSensor.class);
	
	public boolean inside;
}
