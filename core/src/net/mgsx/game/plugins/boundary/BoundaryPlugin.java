package net.mgsx.game.plugins.boundary;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.boundary.systems.BoundarySystem;

@PluginDef(components={BoundaryComponent.class})
public class BoundaryPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new BoundarySystem());
	}

}
