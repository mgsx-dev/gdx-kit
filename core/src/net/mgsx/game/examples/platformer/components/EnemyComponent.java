package net.mgsx.game.examples.platformer.components;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.LogicBehavior;
import net.mgsx.game.core.components.LogicComponent;
import net.mgsx.game.examples.platformer.systems.behaviors.EnemyBehavior;

/**
 * Just keet track of global enemy properties (is alive, life, points ...)
 * 
 * @author mgsx
 *
 */
@Storable("example.platformer.enemy")
@EditableComponent
public class EnemyComponent extends LogicComponent
{
	public boolean alive;
	@Override
	protected LogicBehavior createBehavior() {
		return new EnemyBehavior();
	}

}