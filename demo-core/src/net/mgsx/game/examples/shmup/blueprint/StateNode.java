package net.mgsx.game.examples.shmup.blueprint;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public interface StateNode {
	
	public void update(Engine engine, Entity entity, float deltaTime);
	
}
