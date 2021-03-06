package net.mgsx.game.examples.raycast;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.raycast.systems.AuditorRenderSystem;
import net.mgsx.game.examples.raycast.systems.CompassLocalSystem;
import net.mgsx.game.examples.raycast.systems.RayCasterBox2DSystem;
import net.mgsx.game.examples.raycast.systems.RayCasterDebugSystem;
import net.mgsx.game.examples.raycast.systems.SpatialAudioSystem;
import net.mgsx.game.plugins.DefaultEditorPlugin;


public class RayCastExamplePlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.entityEngine.addSystem(new RayCasterBox2DSystem());
		editor.entityEngine.addSystem(new RayCasterDebugSystem());
		editor.entityEngine.addSystem(new CompassLocalSystem());
		editor.entityEngine.addSystem(new AuditorRenderSystem());
		editor.entityEngine.addSystem(new SpatialAudioSystem());
	}
	
}
