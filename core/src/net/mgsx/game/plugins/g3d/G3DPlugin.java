package net.mgsx.game.plugins.g3d;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.components.TextureAnimationComponent;
import net.mgsx.game.plugins.g3d.systems.G3DAnimationSystem;
import net.mgsx.game.plugins.g3d.systems.G3DCullingSystem;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;
import net.mgsx.game.plugins.g3d.systems.G3DTextureAnimationSystem;
import net.mgsx.game.plugins.g3d.systems.G3DTransformSystem;

@PluginDef(components={G3DModel.class, TextureAnimationComponent.class})

public class G3DPlugin implements Plugin
{
	@Override
	public void initialize(final GameScreen engine) 
	{
		// serializers
		engine.registry.addSerializer(ModelInstance.class, new ModelInstanceSerializer());
		
		// systems
		
		engine.entityEngine.addSystem(new G3DAnimationSystem());
		engine.entityEngine.addSystem(new G3DTransformSystem());
		engine.entityEngine.addSystem(new G3DCullingSystem(engine));
		engine.entityEngine.addSystem(new G3DRendererSystem(engine));
		engine.entityEngine.addSystem(new G3DTextureAnimationSystem());
	}
}
