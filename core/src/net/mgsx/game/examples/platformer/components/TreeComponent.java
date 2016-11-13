package net.mgsx.game.examples.platformer.components;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.LogicBehavior;
import net.mgsx.game.core.components.LogicComponent;
import net.mgsx.game.examples.platformer.systems.behaviors.TreeBehavior;

@Storable("example.platformer.tree")
@EditableComponent
public class TreeComponent extends LogicComponent
{

	@Override
	protected LogicBehavior createBehavior() {
		return new TreeBehavior();
	}
}
