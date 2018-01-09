package net.mgsx.game.plugins.g3d;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.g3d.components.DirectionalLightComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.components.LightNoiseComponent;
import net.mgsx.game.plugins.g3d.components.PointLightComponent;
import net.mgsx.game.plugins.g3d.components.ShadowCasting;
import net.mgsx.game.plugins.g3d.components.TextureAnimationComponent;
import net.mgsx.game.plugins.g3d.systems.G3DAnimationSystem;
import net.mgsx.game.plugins.g3d.systems.G3DCullingSystem;
import net.mgsx.game.plugins.g3d.systems.G3DLightCullingSystem;
import net.mgsx.game.plugins.g3d.systems.G3DLightNoiseSystem;
import net.mgsx.game.plugins.g3d.systems.G3DRendererDeferredSystem;
import net.mgsx.game.plugins.g3d.systems.G3DRendererSystem;
import net.mgsx.game.plugins.g3d.systems.G3DTextureAnimationSystem;
import net.mgsx.game.plugins.g3d.systems.G3DTransformSystem;
import net.mgsx.game.plugins.graphics.GraphicsPlugin;

@PluginDef(components={
		G3DModel.class, 
		TextureAnimationComponent.class,
		DirectionalLightComponent.class,
		LightNoiseComponent.class,
		PointLightComponent.class,
		ShadowCasting.class},
		dependencies=GraphicsPlugin.class)

public class G3DPlugin implements Plugin
{
	public static boolean defferedRendering = false;
	
	@Override
	public void initialize(final GameScreen engine) 
	{
		// serializers
		engine.registry.addSerializer(ModelInstance.class, new ModelInstanceSerializer());
		
		// systems
		
		engine.entityEngine.addSystem(new G3DAnimationSystem());
		engine.entityEngine.addSystem(new G3DTransformSystem());
		engine.entityEngine.addSystem(new G3DCullingSystem(engine));
		
		if(defferedRendering)
			engine.entityEngine.addSystem(new G3DRendererDeferredSystem(engine));
		else
			engine.entityEngine.addSystem(new G3DRendererSystem(engine));
		
		engine.entityEngine.addSystem(new G3DTextureAnimationSystem());
		engine.entityEngine.addSystem(new G3DLightCullingSystem(engine));
		engine.entityEngine.addSystem(new G3DLightNoiseSystem());
	}
}
