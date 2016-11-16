package net.mgsx.game.plugins.controller;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.controller.components.MoveControl;
import net.mgsx.game.plugins.controller.systems.MoveControlSystem;

@PluginDef(components=MoveControl.class)
public class ControllerPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new MoveControlSystem());
	}

}
