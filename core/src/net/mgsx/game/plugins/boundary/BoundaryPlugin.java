package net.mgsx.game.plugins.boundary;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.components.BoundaryComponent;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.systems.BoundarySystem;

public class BoundaryPlugin implements Plugin
{

	@Override
	public void initialize(GameEngine engine) 
	{
		Storage.register(BoundaryComponent.class, "boundary");
		
		engine.entityEngine.addSystem(new BoundarySystem(engine));
	}

}
