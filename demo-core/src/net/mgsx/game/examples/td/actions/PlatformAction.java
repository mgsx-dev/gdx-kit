package net.mgsx.game.examples.td.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import net.mgsx.game.examples.td.components.Platform;
import net.mgsx.game.examples.td.systems.MapSystem;

public class PlatformAction extends AbstractTileAction
{

	public PlatformAction(Engine engine) {
		super(engine);
	}

	@Override
	public boolean allowed() {
		return true; // TODO may have a coast ?
	}

	@Override
	public void apply(int x, int y) 
	{
		MapSystem map = getEngine().getSystem(MapSystem.class);
		Entity cell = map.getOrCreateTile(x, y);
		cell.add(getEngine().createComponent(Platform.class));
	}

}
