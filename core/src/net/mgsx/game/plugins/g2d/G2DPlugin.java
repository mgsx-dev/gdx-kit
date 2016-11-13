package net.mgsx.game.plugins.g2d;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.g2d.components.Animation2DComponent;
import net.mgsx.game.plugins.g2d.components.SpriteModel;
import net.mgsx.game.plugins.g2d.systems.Animation2DSystem;
import net.mgsx.game.plugins.g2d.systems.G2DBoundarySystem;
import net.mgsx.game.plugins.g2d.systems.G2DRenderSystem;
import net.mgsx.game.plugins.g2d.systems.G2DTransformSystem;

@PluginDef(components={
		SpriteModel.class,
		Animation2DComponent.class
})
public class G2DPlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new G2DTransformSystem());
		engine.entityEngine.addSystem(new G2DRenderSystem(engine));
		engine.entityEngine.addSystem(new G2DBoundarySystem());
		engine.entityEngine.addSystem(new Animation2DSystem());
	}

}
