package net.mgsx.box2d.editor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.NativeService;
import net.mgsx.game.plugins.box2d.Box2DPlugin;
import net.mgsx.game.plugins.btree.BTreePlugin;
import net.mgsx.game.plugins.g3d.ModelPlugin;
import net.mgsx.game.plugins.parallax.ParallaxPlugin;
import net.mgsx.game.plugins.profiling.ProfilerPlugin;
import net.mgsx.game.plugins.sprite.SpritePlugin;

public class EditorLauncher 
{
	public static void main (String[] args) {
		
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
		
		new LwjglApplication(editor, config);
	}
}
