package net.mgsx.game.plugins;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.plugins.boundary.BoundaryPlugin;
import net.mgsx.game.plugins.camera.CameraPlugin;
import net.mgsx.game.plugins.controller.ControllerPlugin;
import net.mgsx.game.plugins.core.CorePlugin;
import net.mgsx.game.plugins.g2d.G2DPlugin;
import net.mgsx.game.plugins.g3d.G3DPlugin;
import net.mgsx.game.plugins.graphics.GraphicsPlugin;
import net.mgsx.game.plugins.p3d.Particle3DPlugin;
import net.mgsx.game.plugins.parallax.ParallaxPlugin;
import net.mgsx.game.plugins.particle2d.Particle2DPlugin;
import net.mgsx.game.plugins.spline.SplinePlugin;
import net.mgsx.game.plugins.tiles.TilesPlugin;

/**
 *  Default plugin configuration, import all available runtime plugins
 * 
 * @author mgsx
 *
 */
@PluginDef(dependencies={
	GraphicsPlugin.class,
	BoundaryPlugin.class,
	ControllerPlugin.class,
	CorePlugin.class,
	G2DPlugin.class,
	G3DPlugin.class,
	ParallaxPlugin.class,
	Particle2DPlugin.class,
	SplinePlugin.class,
	TilesPlugin.class,
	CameraPlugin.class,
	ControllerPlugin.class,
	Particle3DPlugin.class
})
public interface DefaultPlugin {

}
