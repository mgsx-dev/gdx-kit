package net.mgsx.game.examples.openworld;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldDebugSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldEnvSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldLandRenderSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldManagerSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldSkySystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldWaterRenderSystem;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.bullet.system.BulletWorldDebugSystem;
import net.mgsx.game.plugins.procedural.systems.HeightFieldDebugSystem;

public class OpenWorldEditorPlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.entityEngine.addSystem(new OpenWorldManagerSystem());
		editor.entityEngine.addSystem(new OpenWorldDebugSystem());
		
		// TODO non edit part
		editor.entityEngine.addSystem(new OpenWorldLandRenderSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldCameraSystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldSkySystem(editor.game));
		editor.entityEngine.addSystem(new OpenWorldEnvSystem());
		editor.entityEngine.addSystem(new OpenWorldWaterRenderSystem(editor.game));
		
		// XXX
		editor.entityEngine.getSystem(BulletWorldDebugSystem.class).setProcessing(false);
		editor.entityEngine.getSystem(HeightFieldDebugSystem.class).setProcessing(false);
	}

}
