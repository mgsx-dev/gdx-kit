package net.mgsx.game.examples.shmup;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.shmup.system.ShmupEnemySystem;
import net.mgsx.game.examples.shmup.system.ShmupPlayerBulletSystem;
import net.mgsx.game.examples.shmup.system.ShmupPlayerSystem;
import net.mgsx.game.examples.shmup.system.ShmupRenderSystem;
import net.mgsx.game.plugins.box2d.Box2DEditorPlugin;
import net.mgsx.game.plugins.controller.ControllerPlugin;
import net.mgsx.game.plugins.editor.KitEditorPlugin;
import net.mgsx.game.plugins.g2d.G2DEditorPlugin;
import net.mgsx.game.plugins.g3d.G3DEditorPlugin;

@PluginDef(dependencies={KitEditorPlugin.class, Box2DEditorPlugin.class, ControllerPlugin.class, G2DEditorPlugin.class, G3DEditorPlugin.class})
public class ShmupPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addSystem(new ShmupEnemySystem());
		editor.addSystem(new ShmupPlayerSystem());
		editor.addSystem(new ShmupPlayerBulletSystem());
		editor.addSystem(new ShmupRenderSystem());
	}

}
