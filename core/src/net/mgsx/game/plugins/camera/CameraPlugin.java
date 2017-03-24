package net.mgsx.game.plugins.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.plugins.camera.components.ActiveCamera;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.ViewportComponent;
import net.mgsx.game.plugins.camera.storage.CameraSerializer;
import net.mgsx.game.plugins.camera.storage.ViewportSerializer;
import net.mgsx.game.plugins.camera.systems.CameraSystem;
import net.mgsx.game.plugins.camera.systems.CameraTransformSystem;

@PluginDef(category="graphics", components={
		CameraComponent.class, ActiveCamera.class, ViewportComponent.class})
public class CameraPlugin implements Plugin
{
	@Override
	public void initialize(GameScreen engine) 
	{
		engine.entityEngine.addSystem(new CameraTransformSystem());
		engine.entityEngine.addSystem(new CameraSystem(engine));
		
		engine.registry.serializers.put(Camera.class, new CameraSerializer());
		engine.registry.serializers.put(PerspectiveCamera.class, new CameraSerializer());
		engine.registry.serializers.put(OrthographicCamera.class, new CameraSerializer());
		
		engine.registry.serializers.put(Viewport.class, new ViewportSerializer());
		engine.registry.serializers.put(ScreenViewport.class, new ViewportSerializer());
		engine.registry.serializers.put(FitViewport.class, new ViewportSerializer());
		engine.registry.serializers.put(FillViewport.class, new ViewportSerializer());
		engine.registry.serializers.put(StretchViewport.class, new ViewportSerializer());
		engine.registry.serializers.put(ExtendViewport.class, new ViewportSerializer());
	}
	
	
}
