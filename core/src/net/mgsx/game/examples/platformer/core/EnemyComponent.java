package net.mgsx.game.examples.platformer.core;

/**
 * Just keet track of global enemy properties (is alive, life, points ...)
 * 
 * @author mgsx
 *
 */
public class EnemyComponent extends LogicComponent
{

	@Override
	protected LogicBehavior createBehavior() {
		return new EnemyBehavior();
	}

}