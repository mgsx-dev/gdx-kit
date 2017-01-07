package net.mgsx.game.examples.crafting;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.examples.crafting.systems.VoxelWorldSystem;
import net.mgsx.game.plugins.DefaultPlugin;

/**
 * 
 * Game core plugin
 * 
 * @author mgsx
 *
 */
@PluginDef(components={})
public class CraftingPlugin implements Plugin, DefaultPlugin
{
	public GameScreen engine;
	
	@Override
	public void initialize(GameScreen engine) 
	{
		this.engine = engine;
		
		engine.entityEngine.addSystem(new VoxelWorldSystem(engine));
	}

	

}
