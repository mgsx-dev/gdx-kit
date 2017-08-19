package net.mgsx.game.plugins;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.plugins.box2d.Box2DEditorPlugin;
import net.mgsx.game.plugins.btree.BTreePlugin;
import net.mgsx.game.plugins.bullet.BulletEditorPlugin;
import net.mgsx.game.plugins.fsm.StateMachineEditorPlugin;
import net.mgsx.game.plugins.pd.PdEditorPlugin;

/**
 *  Full plugin configuration, import all available and optinal editor and runtime plugins
 * 
 * @author mgsx
 *
 */
@PluginDef(dependencies={
	Box2DEditorPlugin.class,
	BulletEditorPlugin.class,
	BTreePlugin.class,
	PdEditorPlugin.class,
	StateMachineEditorPlugin.class
})
public interface FullEditorPlugin extends DefaultEditorPlugin {

}
