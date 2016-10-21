package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.components.Duplicable;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.plugins.Initializable;

/**
 * Just keet track of global enemy properties (is alive, life, points ...)
 * 
 * @author mgsx
 *
 */
public class EnemyComponent  implements Component, Initializable, Duplicable
{
	private Engine manager;
	private Entity entity;
	
	@Override
	public void initialize(Engine manager, Entity entity) {
		this.entity = entity;
		this.manager = manager;
//		entity.getComponent(G3DModel.class).animationController.paused = false;
//		entity.getComponent(G3DModel.class).animationController.setAnimation("apple.lp|apple.lpAction", -1);
		entity.add(new Transform2DComponent());
	}
	


	@Override
	public Component duplicate() {
		EnemyComponent clone = new EnemyComponent();
		return clone;
	}

	
}