package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.core.Editor;
import net.mgsx.core.NativeService;
import net.mgsx.examples.platformer.core.PlatformerPlugin;
import net.mgsx.examples.platformer.editor.PlatformerGameEditor;
import net.mgsx.plugins.box2d.Box2DPlugin;
import net.mgsx.plugins.btree.BTreePlugin;
import net.mgsx.plugins.g3d.ModelPlugin;
import net.mgsx.plugins.parallax.ParallaxPlugin;
import net.mgsx.plugins.profiling.ProfilerPlugin;
import net.mgsx.plugins.sprite.SpritePlugin;

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
		editor.registerPlugin(new Box2DPlugin());
		editor.registerPlugin(new BTreePlugin());
		editor.registerPlugin(new PlatformerPlugin());
		editor.registerPlugin(new PlatformerGameEditor());
		
		new LwjglApplication(editor, config);
	}
}
