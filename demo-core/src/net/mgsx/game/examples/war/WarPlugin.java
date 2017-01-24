package net.mgsx.game.examples.war;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.examples.war.system.WarLogicSystem;

public class WarPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new WarLogicSystem());
	}

}
