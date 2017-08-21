package net.mgsx.game.examples.openworld;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.GLVersion.Type;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.openworld.components.LandMeshComponent;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.components.OpenWorldCamera;
import net.mgsx.game.examples.openworld.components.TreesComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldRuntimeSettings;
import net.mgsx.game.examples.openworld.systems.OpenLifeAnimalSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldAudioSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraPathSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldDebugSystem;
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
import net.mgsx.game.examples.openworld.tools.AddElementTool;
import net.mgsx.game.examples.openworld.tools.AlignMeshTool;
import net.mgsx.game.examples.openworld.tools.CraftTransformTool;
import net.mgsx.game.examples.openworld.tools.MoveElementTool;
import net.mgsx.game.examples.openworld.tools.RemoveElementTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.bullet.BulletEditorPlugin;
import net.mgsx.game.plugins.bullet.system.BulletWorldDebugSystem;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;
import net.mgsx.game.plugins.g3d.systems.G3DCullingSystem;
import net.mgsx.game.plugins.procedural.systems.HeightFieldDebugSystem;

@PluginDef(components={
		LandMeshComponent.class,
		ObjectMeshComponent.class,
		OpenWorldCamera.class,
		TreesComponent.class
	},
	dependencies={
		BulletEditorPlugin.class
	})
public class OpenWorldEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) 
	{
		// query hardware capabilities in order to configure systems dynamically.
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.app.debug("OW", Gdx.graphics.getGLVersion().getDebugVersionString());
		
		boolean highQuality = false;
		if(Gdx.graphics.getGLVersion().getType() == Type.OpenGL){
			if(Gdx.graphics.getGLVersion().isVersionEqualToOrHigher(4, 0)){
				highQuality = true;
			}
		}
		OpenWorldRuntimeSettings.highQuality = highQuality;
		editor.entityEngine.addSystem(new OpenWorldAudioSystem());

		editor.entityEngine.addSystem(new OpenWorldPlayerSensorSystem());
		// XXX
		editor.entityEngine.getSystem(OpenWorldPlayerSensorSystem.class).setProcessing(false);
		
		
		editor.entityEngine.addSystem(new OpenWorldGameSystem());
		
		editor.entityEngine.addSystem(new OpenWorldGeneratorSystem());
		editor.entityEngine.addSystem(new OpenWorldManagerSystem());
		editor.entityEngine.addSystem(new OpenWorldSpawnSystem());
		editor.entityEngine.addSystem(new OpenWorldSpawnAnimalSystem());
		editor.entityEngine.addSystem(new OpenWorldDebugSystem());
		
		editor.entityEngine.addSystem(new OpenWorldPhysicSystem());

		
		
		editor.addTool(new AlignMeshTool());
		editor.addTool(new AddElementTool());
		editor.addTool(new RemoveElementTool());
		editor.addTool(new MoveElementTool());
		editor.addTool(new CraftTransformTool());
		
		// TODO non edit part
		editor.entityEngine.addSystem(new OpenWorldLandRenderSystem());
		editor.entityEngine.addSystem(new OpenWorldCameraPathSystem());
		editor.entityEngine.addSystem(new OpenWorldCameraSystem());
		editor.entityEngine.addSystem(new OpenWorldSkySystem());
		editor.entityEngine.addSystem(new OpenWorldEnvSystem());
		
		editor.entityEngine.addSystem(new OpenWorldWaterLQRenderSystem());
		if(highQuality){
			editor.entityEngine.addSystem(new OpenWorldWaterRenderSystem());
			editor.entityEngine.getSystem(OpenWorldWaterLQRenderSystem.class).setProcessing(false);
		}
		
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
		editor.entityEngine.getSystem(BulletWorldDebugSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(HeightFieldDebugSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(OpenWorldMapSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(G3DCullingSystem.class).culling = false;
		
		// XXX disable default selectors !
		// best way would be to disable select tool instead ...
		editor.entityEngine.getSystem(SelectionSystem.class).selectors.clear();
	}

}
