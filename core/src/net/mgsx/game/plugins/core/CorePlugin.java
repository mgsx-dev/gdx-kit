package net.mgsx.game.plugins.core;

import com.badlogic.gdx.graphics.Texture;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.AssetSerializer;
import net.mgsx.game.plugins.core.components.EntityEmitter;
import net.mgsx.game.plugins.core.components.PolygonComponent;
import net.mgsx.game.plugins.core.components.ProxyComponent;
import net.mgsx.game.plugins.core.components.Rotate2DAnimation;
import net.mgsx.game.plugins.core.components.SlavePhysics;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.core.systems.EntityEmitterSystem;
import net.mgsx.game.plugins.core.systems.ExpirySystem;
import net.mgsx.game.plugins.core.systems.Rotation2DSystem;
import net.mgsx.game.plugins.core.systems.Translation3DSystem;

@PluginDef(components={
		ProxyComponent.class, 
		Transform2DComponent.class, 
		PolygonComponent.class,
		Rotate2DAnimation.class,
		EntityEmitter.class,
		SlavePhysics.class})
public class CorePlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		// basic type serializers
		engine.registry.addSerializer(Texture.class, new AssetSerializer<Texture>(Texture.class));
		
		engine.entityEngine.addSystem(new Translation3DSystem());
		engine.entityEngine.addSystem(new Rotation2DSystem());
		engine.entityEngine.addSystem(new EntityEmitterSystem(engine));
		engine.entityEngine.addSystem(new ExpirySystem());
		
	}

}
