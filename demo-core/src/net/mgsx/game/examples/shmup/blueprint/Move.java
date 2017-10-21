package net.mgsx.game.examples.shmup.blueprint;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.blueprint.annotations.Inlet;
import net.mgsx.game.blueprint.annotations.Outlet;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.examples.shmup.component.Enemy;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@Inlet
public class Move implements StateNode
{
	@Outlet public transient TransitionNode next;
	
	@Editable
	public Vector2 velocity = new Vector2();

	@Override
	public void update(Engine engine, Entity entity, float deltaTime) 
	{
		Enemy enemy = Enemy.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(transform != null){
			transform.position.mulAdd(velocity, deltaTime);
		}
		if(next != null && next.isActive(engine, entity) && enemy != null){
			enemy.replace(this, next.next);
		}
	}

}
