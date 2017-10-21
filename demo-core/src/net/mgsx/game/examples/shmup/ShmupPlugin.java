package net.mgsx.game.examples.shmup;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.shmup.system.ShmupEmitterSystem;
import net.mgsx.game.examples.shmup.system.ShmupEnemySystem;
import net.mgsx.game.plugins.box2d.Box2DEditorPlugin;
import net.mgsx.game.plugins.editor.KitEditorPlugin;

@PluginDef(dependencies={KitEditorPlugin.class, Box2DEditorPlugin.class})
public class ShmupPlugin extends EditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) 
	{
		editor.addSystem(new ShmupEmitterSystem());
		editor.addSystem(new ShmupEnemySystem());
		
	}

}
