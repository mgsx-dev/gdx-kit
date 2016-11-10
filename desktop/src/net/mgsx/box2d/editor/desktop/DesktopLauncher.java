package net.mgsx.box2d.editor.desktop;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.plugins.box2dold.Box2DEditor;
import net.mgsx.game.plugins.sprite.SpritePlugin;

public class DesktopLauncher 
{
	public static void main (String[] args) {
		
		DesktopNativeInterface nativeInsterface = new DesktopNativeInterface();
		NativeService.instance = nativeInsterface; 
		
		File file = null;
		if(args.length > 0){
			file = new File(args[0]);
			
			if(file.exists())
			{
				nativeInsterface.path = file.getParent();
			}
			else
			{
				nativeInsterface.path = new File(".").getPath();
				System.err.println("warning : file not found " + file.getPath());
			}
		}
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Box2DEditor editor = new Box2DEditor(file.getAbsolutePath());
		editor.registerPlugin(new SpritePlugin());
		new LwjglApplication(editor, config);
	}
}
