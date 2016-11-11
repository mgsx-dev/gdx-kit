package net.mgsx.game.plugins.boundary;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.boundary.systems.BoundarySystem;

public class BoundaryPlugin implements Plugin
{

	@Override
	public void initialize(GameEngine engine) 
	{
		engine.register(BoundaryComponent.class);
		
		engine.entityEngine.addSystem(new BoundarySystem(engine));
	}

}
