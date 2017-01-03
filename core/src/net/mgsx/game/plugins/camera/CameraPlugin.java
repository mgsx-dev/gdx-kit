package net.mgsx.game.plugins.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.storage.CameraSerializer;
import net.mgsx.game.plugins.camera.systems.CameraSystem;
import net.mgsx.game.plugins.camera.systems.CameraTransformSystem;

@PluginDef(components={
		CameraComponent.class})
public class CameraPlugin implements Plugin
{
	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new CameraTransformSystem());
		engine.entityEngine.addSystem(new CameraSystem(engine));
		
		engine.registry.serializers.put(PerspectiveCamera.class, new CameraSerializer());
		engine.registry.serializers.put(OrthographicCamera.class, new CameraSerializer());
	}
	
	
}
