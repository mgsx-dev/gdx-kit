package net.mgsx.game.examples.shmup.blueprint;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.blueprint.annotations.Outlet;

public abstract class TransitionNode implements ShmupNode {
	
	@Outlet public transient StateNode next;
	
	public abstract boolean isActive(Engine engine, Entity entity);
}
