package net.mgsx.game.plugins.core;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.core.components.PolygonComponent;

public class CorePlugin implements Plugin
{

	@Override
	public void initialize(GameEngine engine) 
	{
		engine.register(Transform2DComponent.class);
		engine.register(PolygonComponent.class);
	}

}
