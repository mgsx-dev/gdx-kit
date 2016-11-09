package net.mgsx.game.examples.platformer.core.states;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.plugins.fsm.components.StateComponent;

@EditableComponent("Flying State")

@Storable(tag="example.platformer.flying")

public class FlyingState implements StateComponent
{
	public float time;
}
