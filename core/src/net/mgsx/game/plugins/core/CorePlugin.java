package net.mgsx.game.plugins.core;

import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.AssetSerializer;
import net.mgsx.game.core.storage.EntityGroupRef;
import net.mgsx.game.core.storage.EntityGroupRefSerializer;
import net.mgsx.game.plugins.core.components.EntityEmitter;
import net.mgsx.game.plugins.core.components.PolygonComponent;
import net.mgsx.game.plugins.core.components.ProxyComponent;
import net.mgsx.game.plugins.core.components.Rotate2DAnimation;
import net.mgsx.game.plugins.core.components.SlavePhysics;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.core.storage.ProxySerializer;
import net.mgsx.game.plugins.core.systems.ClearScreenSystem;
import net.mgsx.game.plugins.core.systems.DependencySystem;
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
		engine.assets.setLoader(ShaderProgram.class, new ShaderProgramLoader(engine.assets.getFileHandleResolver(), "-vertex.glsl", "-fragment.glsl"));
		
		// basic type serializers
		engine.registry.addSerializer(Texture.class, new AssetSerializer<Texture>(Texture.class));
		
		engine.entityEngine.addSystem(new Translation3DSystem());
		engine.entityEngine.addSystem(new Rotation2DSystem());
		engine.entityEngine.addSystem(new EntityEmitterSystem(engine));
		engine.entityEngine.addSystem(new ExpirySystem());
		engine.entityEngine.addSystem(new DependencySystem());
		engine.entityEngine.addSystem(new ClearScreenSystem());
		
		engine.registry.addSerializer(ProxyComponent.class, new ProxySerializer());
		engine.registry.addSerializer(EntityGroupRef.class, new EntityGroupRefSerializer());
	}

}
