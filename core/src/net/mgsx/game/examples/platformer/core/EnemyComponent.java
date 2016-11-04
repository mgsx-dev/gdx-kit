package net.mgsx.game.examples.platformer.core;

import net.mgsx.game.core.components.LogicBehavior;
import net.mgsx.game.core.components.LogicComponent;

/**
 * Just keet track of global enemy properties (is alive, life, points ...)
 * 
 * @author mgsx
 *
 */
public class EnemyComponent extends LogicComponent
{
	public boolean alive;
	@Override
	protected LogicBehavior createBehavior() {
		return new EnemyBehavior();
	}

}