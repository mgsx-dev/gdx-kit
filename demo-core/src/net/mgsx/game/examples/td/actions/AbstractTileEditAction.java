package net.mgsx.game.examples.td.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

public abstract class AbstractTileEditAction extends Action
{

	public AbstractTileEditAction(Engine engine) {
		super(engine);
	}
	
	public abstract void apply(Entity cell);

}
