package net.mgsx.box2d.editor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.core.Editor;
import net.mgsx.core.NativeService;
import net.mgsx.plugins.parallax.ParallaxPlugin;
import net.mgsx.plugins.sprite.SpritePlugin;

public class EditorLauncher 
{
	public static void main (String[] args) {
		
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Editor editor = new Editor();
		
		editor.registerPlugin(new ParallaxPlugin());
		editor.registerPlugin(new SpritePlugin());
		
		new LwjglApplication(editor, config);
	}
}
