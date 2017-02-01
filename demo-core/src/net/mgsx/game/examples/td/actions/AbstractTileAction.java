package net.mgsx.game.examples.td.actions;

import com.badlogic.ashley.core.Engine;

/**
 * Action with map interaction (click on map action)
 * @author mgsx
 *
 */
public abstract class AbstractTileAction extends Action
{
	public AbstractTileAction(Engine engine) {
		super(engine);
	}

	abstract public void apply(int x, int y);
}
