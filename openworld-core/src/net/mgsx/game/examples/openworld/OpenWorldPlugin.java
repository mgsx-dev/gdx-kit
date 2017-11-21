package net.mgsx.game.examples.openworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.GLVersion.Type;

import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.OpenWorldCamera;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldRuntimeSettings;
import net.mgsx.game.examples.openworld.systems.OpenLifeAnimalSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldAudioSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraPathSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldEnvSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldFaunaSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldGameSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldGeneratorSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldHUDSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldLandRenderSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldLensFlareSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldManagerSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldMapSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldPhysicSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldPlayerSensorSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldRainSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldSkySystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldSpawnAnimalSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldSpawnSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldTimeSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldTreeSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldWaterLQRenderSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldWaterRenderSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectUpdateSystem;
import net.mgsx.game.examples.openworld.systems.WeatherSystem;
import net.mgsx.game.plugins.DefaultPlugin;
import net.mgsx.game.plugins.bullet.BulletEditorPlugin;
import net.mgsx.game.plugins.g3d.systems.G3DCullingSystem;

@PluginDef(components={
		LandMeshComponent.class,
		ObjectMeshComponent.class,
		OpenWorldCamera.class,
		TreesComponent.class
	},
	dependencies={
		BulletEditorPlugin.class
	})
public class OpenWorldPlugin implements Plugin, DefaultPlugin
{
	@Override
	public void initialize(GameScreen editor)
	{
		// query hardware capabilities in order to configure systems dynamically.
		
		Gdx.app.log("OW", Gdx.graphics.getGLVersion().getDebugVersionString());
		
		if(OpenWorldRuntimeSettings.autoQuality){
			boolean highQuality = false;
			// TODO not really true, maybe openGL 3 could be ok
			if(Gdx.graphics.getGLVersion().getType() == Type.OpenGL){
				if(Gdx.graphics.getGLVersion().isVersionEqualToOrHigher(4, 0)){
					highQuality = true;
				}
			}
			OpenWorldRuntimeSettings.highQuality = highQuality;
		}
		Gdx.app.log("OW", "GPU quality used is " + (OpenWorldRuntimeSettings.highQuality ? "high" : "low"));
		
		editor.entityEngine.addSystem(new OpenWorldAudioSystem());

		editor.entityEngine.addSystem(new OpenWorldPlayerSensorSystem());
		// XXX
		editor.entityEngine.getSystem(OpenWorldPlayerSensorSystem.class).setProcessing(false);
		
		
		editor.entityEngine.addSystem(new OpenWorldGameSystem());
		
		editor.entityEngine.addSystem(new OpenWorldGeneratorSystem());
		editor.entityEngine.addSystem(new OpenWorldManagerSystem());
		editor.entityEngine.addSystem(new OpenWorldSpawnSystem());
		editor.entityEngine.addSystem(new OpenWorldSpawnAnimalSystem());
		
		editor.entityEngine.addSystem(new OpenWorldPhysicSystem());

		
		
		// TODO non edit part
		editor.entityEngine.addSystem(new OpenWorldLandRenderSystem());
		editor.entityEngine.addSystem(new OpenWorldCameraPathSystem());
		editor.entityEngine.addSystem(new OpenWorldCameraSystem());
		editor.entityEngine.addSystem(new OpenWorldSkySystem());
		editor.entityEngine.addSystem(new OpenWorldEnvSystem());
		
		editor.entityEngine.addSystem(new OpenWorldWaterRenderSystem());
		editor.entityEngine.addSystem(new OpenWorldWaterLQRenderSystem());
		
		editor.entityEngine.addSystem(new OpenWorldLensFlareSystem());
		editor.entityEngine.addSystem(new OpenWorldTreeSystem());
		editor.entityEngine.addSystem(new OpenWorldMapSystem());
		editor.entityEngine.addSystem(new WeatherSystem());
		editor.entityEngine.addSystem(new UserObjectSystem());
		editor.entityEngine.addSystem(new OpenWorldRainSystem());
		editor.entityEngine.addSystem(new OpenWorldTimeSystem());
		editor.entityEngine.addSystem(new OpenWorldFaunaSystem());
		editor.entityEngine.addSystem(new UserObjectUpdateSystem());
		
		editor.entityEngine.addSystem(new OpenLifeAnimalSystem());
		
		editor.entityEngine.addSystem(new OpenWorldHUDSystem());
		
		// XXX
		editor.entityEngine.getSystem(G3DCullingSystem.class).culling = false;
		
	}

}
