package net.mgsx.game.examples.voxel;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.examples.voxel.systems.VoxelWorldSystem;
import net.mgsx.game.plugins.DefaultPlugin;

/**
 * 
 * Game core plugin
 * 
 * @author mgsx
 *
 */
@PluginDef(components={})
public class VoxelPlugin implements Plugin, DefaultPlugin
{
	public GameScreen engine;
	
	@Override
	public void initialize(GameScreen engine) 
	{
		this.engine = engine;
		
		engine.entityEngine.addSystem(new VoxelWorldSystem(engine));
	}

	

}
