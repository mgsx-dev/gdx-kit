package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.examples.platformer.PlatformerEditorPlugin;

/**
 * Desktop launcher for level editor
 * @author mgsx
 *
 */
public class PlatformerLevelEditorLauncher {

	public static void main (String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugin = new PlatformerEditorPlugin();
		editConfig.root = args.length > 0 ? args[0] : null;
		editConfig.path = args.length > 1 ? args[1] : null;
		
		new LwjglApplication(new EditorApplication(editConfig), config);
	}
}
