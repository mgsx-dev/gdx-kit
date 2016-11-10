package net.mgsx.game.plugins.core;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.plugins.Plugin;

public class CorePlugin implements Plugin
{

	@Override
	public void initialize(GameEngine engine) 
	{
		engine.register(Transform2DComponent.class);
	}

}
