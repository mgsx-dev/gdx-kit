package net.mgsx.game.plugins;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.plugins.ashley.AshleyEditorPlugin;
import net.mgsx.game.plugins.assets.AssetsEditorPlugin;
import net.mgsx.game.plugins.boundary.BoundaryEditorPlugin;
import net.mgsx.game.plugins.box2d.Box2DEditorPlugin;
import net.mgsx.game.plugins.camera.CameraEditorPlugin;
import net.mgsx.game.plugins.core.CoreEditorPlugin;
import net.mgsx.game.plugins.fsm.StateMachineEditorPlugin;
import net.mgsx.game.plugins.g2d.G2DEditorPlugin;
import net.mgsx.game.plugins.g3d.G3DEditorPlugin;
import net.mgsx.game.plugins.particle2d.Particle2DEditorPlugin;
import net.mgsx.game.plugins.profiling.ProfilerPlugin;
import net.mgsx.game.plugins.spline.SplineEditorPlugin;

/**
 *  Default plugin configuration, import all "built-in" editor plugins and core plugins
 * 
 * @author mgsx
 *
 */
@PluginDef(dependencies={
	AshleyEditorPlugin.class,
	BoundaryEditorPlugin.class,
	Box2DEditorPlugin.class,
	CoreEditorPlugin.class,
	G2DEditorPlugin.class,
	G3DEditorPlugin.class,
	ProfilerPlugin.class,
	CameraEditorPlugin.class,
	SplineEditorPlugin.class,
	Particle2DEditorPlugin.class,
	AssetsEditorPlugin.class,
	StateMachineEditorPlugin.class
})
public interface DefaultEditorPlugin extends DefaultPlugin {

}
