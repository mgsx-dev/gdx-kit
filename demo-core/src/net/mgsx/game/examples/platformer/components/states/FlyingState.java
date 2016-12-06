package net.mgsx.game.examples.platformer.components.states;

import com.badlogic.ashley.core.ComponentMapper;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.plugins.fsm.components.StateComponent;

@EditableComponent(name="Flying State")

@Storable("example.platformer.flying")

public class FlyingState implements StateComponent
{
	
	public static ComponentMapper<FlyingState> components = ComponentMapper.getFor(FlyingState.class);
	
	public float wingsActivity; // TODO rename wingActivity
	public boolean rightToLeft;
	public boolean directionChanged;
}
