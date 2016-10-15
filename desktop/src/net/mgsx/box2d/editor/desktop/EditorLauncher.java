package net.mgsx.box2d.editor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.core.Editor;
import net.mgsx.core.GameEngine;
import net.mgsx.core.NativeService;
import net.mgsx.plugins.parallax.ParallaxPlugin;
import net.mgsx.plugins.sprite.SpritePlugin;

public class EditorLauncher 
{
	public static void main (String[] args) {
		
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		GameEngine editor = new Editor();

		// plugins configuration (order is important)
		//
		editor.registerPlugin(new SpritePlugin());
		editor.registerPlugin(new ParallaxPlugin());
		
		new LwjglApplication(editor, config);
	}
}
