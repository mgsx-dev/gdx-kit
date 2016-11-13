package net.mgsx.game.plugins.camera;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.CullingComponent;
import net.mgsx.game.plugins.camera.systems.CameraSystem;

@PluginDef(components={
		CameraComponent.class, 
		CullingComponent.class})
public class CameraPlugin implements Plugin
{
	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new CameraSystem());
	}
	
	
}
