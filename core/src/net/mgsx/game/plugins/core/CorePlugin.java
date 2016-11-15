package net.mgsx.game.plugins.core;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.core.components.EntityEmitter;
import net.mgsx.game.plugins.core.components.PolygonComponent;
import net.mgsx.game.plugins.core.components.ProxyComponent;
import net.mgsx.game.plugins.core.components.Rotate2DAnimation;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.core.systems.EntityEmitterSystem;
import net.mgsx.game.plugins.core.systems.Rotation2DSystem;

@PluginDef(components={
		ProxyComponent.class, 
		Transform2DComponent.class, 
		PolygonComponent.class,
		Rotate2DAnimation.class,
		EntityEmitter.class})
public class CorePlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new Rotation2DSystem());
		engine.entityEngine.addSystem(new EntityEmitterSystem());
	}

}
