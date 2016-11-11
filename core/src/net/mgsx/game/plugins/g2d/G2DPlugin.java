package net.mgsx.game.plugins.g2d;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.g2d.components.Animation2DComponent;
import net.mgsx.game.plugins.g2d.components.SpriteModel;
import net.mgsx.game.plugins.g2d.systems.Animation2DSystem;
import net.mgsx.game.plugins.g2d.systems.G2DBoundarySystem;
import net.mgsx.game.plugins.g2d.systems.G2DRenderSystem;
import net.mgsx.game.plugins.g2d.systems.G2DTransformSystem;

public class G2DPlugin implements Plugin
{

	@Override
	public void initialize(GameEngine engine) 
	{
		engine.register(SpriteModel.class);
		engine.register(Animation2DComponent.class);
		
		engine.entityEngine.addSystem(new G2DTransformSystem());
		engine.entityEngine.addSystem(new G2DRenderSystem(engine));
		engine.entityEngine.addSystem(new G2DBoundarySystem());
		engine.entityEngine.addSystem(new Animation2DSystem());
	}

}
