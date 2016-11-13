package net.mgsx.game.plugins.core;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.components.ProxyComponent;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.core.components.PolygonComponent;

public class CorePlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		engine.register(ProxyComponent.class);
		engine.register(Transform2DComponent.class);
		engine.register(PolygonComponent.class);
	}

}
