package net.mgsx.game.examples.shmup.blueprint;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.examples.shmup.component.Enemy;

public class Init implements StateNode
{
	public @Outlet StateNode next;
	
	@Override
	public void update(Engine engine, Entity entity, float deltaTime) {
		Enemy enemy = Enemy.components.get(entity);
		enemy.replace(this, next);
	}

}
