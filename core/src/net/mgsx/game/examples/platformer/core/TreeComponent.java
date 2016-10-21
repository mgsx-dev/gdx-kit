package net.mgsx.game.examples.platformer.core;

public class TreeComponent extends LogicComponent
{

	@Override
	protected LogicBehavior createBehavior() {
		return new TreeBehavior();
	}
}
