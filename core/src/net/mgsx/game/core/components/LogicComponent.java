package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.plugins.Initializable;

abstract public class LogicComponent implements Component, Initializable, Duplicable
{
	public LogicBehavior behavior;

	@Override
	public void initialize(Engine manager, Entity entity) 
	{
		behavior = createBehavior();
		if(behavior instanceof Initializable){
			((Initializable)behavior).initialize(manager, entity);
		}
	}
	
	@Override
	public Component duplicate(Engine engine) 
	{
		LogicComponent clone = engine.createComponent(this.getClass()); // dynamic component
		clone.behavior = createBehavior();
		return clone;
	}
	
	protected abstract LogicBehavior createBehavior();
}
