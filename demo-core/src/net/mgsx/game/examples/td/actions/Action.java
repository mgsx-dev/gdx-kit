package net.mgsx.game.examples.td.actions;

import com.badlogic.ashley.core.Engine;

public abstract class Action 
{
	final private Engine engine;
	
	public Action(Engine engine) {
		super();
		this.engine = engine;
	}
	
	final public Engine getEngine() {
		return engine;
	}

	/**
	 * @return whether action is allowed or not.
	 */
	public abstract boolean allowed();
}
