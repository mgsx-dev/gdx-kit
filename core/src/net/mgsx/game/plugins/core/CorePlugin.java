package net.mgsx.game.plugins.core;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.core.components.PolygonComponent;
import net.mgsx.game.plugins.core.components.ProxyComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@PluginDef(components={
		ProxyComponent.class, 
		Transform2DComponent.class, 
		PolygonComponent.class})
public class CorePlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
	}

}
