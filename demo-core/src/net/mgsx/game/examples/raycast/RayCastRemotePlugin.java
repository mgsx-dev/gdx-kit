package net.mgsx.game.examples.raycast;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.raycast.systems.CompassRemoteSystem;

public class RayCastRemotePlugin extends EditorPlugin
{
	@Override
	public void initialize(EditorScreen editor) {
		editor.entityEngine.addSystem(new CompassRemoteSystem());
	}
}
