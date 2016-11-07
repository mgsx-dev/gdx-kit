package net.mgsx.game.plugins.fsm;

import com.badlogic.ashley.core.Entity;

public abstract class StateListener 
{
	abstract public void enter(Entity entity);
	abstract public void update(Entity entity, float deltaTime);
	abstract public void exit(Entity entity);
}
