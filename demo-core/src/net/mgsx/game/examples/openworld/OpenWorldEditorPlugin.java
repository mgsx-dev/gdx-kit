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
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraPathSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldDebugSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldEnvSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldFaunaSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldHUDSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldLandRenderSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldLensFlareSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldManagerSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldMapSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldRainSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldSkySystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldTimeSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldTreeSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldWaterLQRenderSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldWaterRenderSystem;
import net.mgsx.game.examples.openworld.systems.ScenarioSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectSystem;
import net.mgsx.game.examples.openworld.systems.UserObjectUpdateSystem;
import net.mgsx.game.examples.openworld.systems.WeatherSystem;
import net.mgsx.game.examples.openworld.tools.AddElementTool;
import net.mgsx.game.examples.openworld.tools.AlignMeshTool;
import net.mgsx.game.examples.openworld.tools.CraftTransformTool;
import net.mgsx.game.examples.openworld.tools.MoveElementTool;
import net.mgsx.game.examples.openworld.tools.RemoveElementTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.bullet.system.BulletWorldDebugSystem;
import net.mgsx.game.plugins.procedural.systems.HeightFieldDebugSystem;

@PluginDef(components={
		LandMeshComponent.class,
		ObjectMeshComponent.class,
		OpenWorldCamera.class,
		TreesComponent.class
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
		
		
		editor.entityEngine.addSystem(new OpenWorldManagerSystem());
		editor.entityEngine.addSystem(new OpenWorldDebugSystem());
		editor.addTool(new AlignMeshTool(editor));
		editor.addTool(new AddElementTool(editor));
		editor.addTool(new RemoveElementTool(editor));
		editor.addTool(new MoveElementTool(editor));
		editor.addTool(new CraftTransformTool(editor));
		
		// TODO non edit part
		editor.entityEngine.addSystem(new OpenWorldLandRenderSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldCameraPathSystem());
		editor.entityEngine.addSystem(new OpenWorldCameraSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldSkySystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldEnvSystem());
		
		editor.entityEngine.addSystem(new OpenWorldWaterLQRenderSystem(editor.game));
		if(highQuality){
			editor.entityEngine.addSystem(new OpenWorldWaterRenderSystem(editor.game));
			editor.entityEngine.getSystem(OpenWorldWaterLQRenderSystem.class).setProcessing(false);
		}
		
		editor.entityEngine.addSystem(new OpenWorldLensFlareSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldTreeSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldMapSystem(editor.game));
		editor.entityEngine.addSystem(new WeatherSystem());
		editor.entityEngine.addSystem(new ScenarioSystem());
		editor.entityEngine.addSystem(new UserObjectSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldRainSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldTimeSystem());
		editor.entityEngine.addSystem(new OpenWorldFaunaSystem(editor.game));
		editor.entityEngine.addSystem(new UserObjectUpdateSystem());
		
		editor.entityEngine.addSystem(new OpenWorldHUDSystem());
		
		// XXX
		editor.entityEngine.getSystem(BulletWorldDebugSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(HeightFieldDebugSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(ScenarioSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(OpenWorldMapSystem.class).setProcessing(false);
	}

}
