package net.mgsx.game.plugins;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.plugins.box2d.Box2DPlugin;
import net.mgsx.game.plugins.bullet.BulletPlugin;
import net.mgsx.game.plugins.fsm.StateMachinePlugin;
import net.mgsx.game.plugins.pd.PdPlugin;

/**
 *  Full plugin configuration, import all available and optional runtime plugins
 * 
 * @author mgsx
 *
 */
@PluginDef(dependencies={
		Box2DPlugin.class,
		BulletPlugin.class,
		PdPlugin.class,
		StateMachinePlugin.class
	})
public interface FullPlugin extends DefaultPlugin {

}
