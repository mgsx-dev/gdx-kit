package net.mgsx.game.examples.platformer.components;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.LogicBehavior;
import net.mgsx.game.core.components.LogicComponent;
import net.mgsx.game.examples.platformer.systems.behaviors.TreeBehavior;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Storable("example.platformer.tree")
@EditableComponent(name="Tree Logic", all={G3DModel.class, Box2DBodyModel.class})
public class TreeComponent extends LogicComponent
{

	@Override
	protected LogicBehavior createBehavior() {
		return new TreeBehavior();
	}
}
