package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.game.core.Editor;
import net.mgsx.game.core.NativeService;
import net.mgsx.game.examples.platformer.core.PlatformerPlugin;
import net.mgsx.game.examples.platformer.editor.PlatformerGameEditor;
import net.mgsx.game.plugins.box2d.Box2DPlugin;
import net.mgsx.game.plugins.btree.BTreePlugin;
import net.mgsx.game.plugins.g3d.ModelPlugin;
import net.mgsx.game.plugins.parallax.ParallaxPlugin;
import net.mgsx.game.plugins.particle2d.Particle2DEditorPlugin;
import net.mgsx.game.plugins.particle2d.Particle2DPlugin;
import net.mgsx.game.plugins.profiling.ProfilerPlugin;
import net.mgsx.game.plugins.spline.SplineEditorPlugin;
import net.mgsx.game.plugins.spline.SplinePlugin;
import net.mgsx.game.plugins.sprite.SpritePlugin;

public class PlatformerGameEditorLauncher {

	public static void main (String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Editor editor = new Editor();

		// plugins configuration (order is important)
		//
		editor.registerPlugin(new ProfilerPlugin());
		editor.registerPlugin(new SpritePlugin());
		editor.registerPlugin(new ParallaxPlugin());
		editor.registerPlugin(new ModelPlugin());
		
		editor.registerPlugin(new SplinePlugin());
		editor.registerPlugin(new SplineEditorPlugin());
		
		editor.registerPlugin(new Particle2DPlugin());
		editor.registerPlugin(new Particle2DEditorPlugin());
		
		editor.registerPlugin(new Box2DPlugin());
		editor.registerPlugin(new BTreePlugin());
		editor.registerPlugin(new PlatformerPlugin());
		editor.registerPlugin(new PlatformerGameEditor());
		
		new LwjglApplication(editor, config);
	}
}
