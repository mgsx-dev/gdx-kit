package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.box2d.editor.desktop.DesktopNativeInterface;
import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.examples.platformer.PlatformerEditorPlugin;
import net.mgsx.gdx.pd.PdAudioDesktop;
import net.mgsx.pd.Pd;

/**
 * Desktop launcher for level editor
 * @author mgsx
 *
 */
public class PlatformerLevelEditorLauncher {

	public static void main (String[] args) 
	{
		DesktopNativeInterface nativeService = new DesktopNativeInterface(); 
		NativeService.instance = nativeService; 
		
		LwjglApplicationConfiguration.disableAudio = true;
		Pd.audio = new PdAudioDesktop();
		Pd.audio.create();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new PlatformerEditorPlugin());
		editConfig.root = args.length > 0 ? args[0] : null;
		editConfig.path = args.length > 1 ? args[1] : null;
		
		if(editConfig.root != null) nativeService.path = editConfig.root;
		
		new LwjglApplication(new EditorApplication(editConfig), config);
	}
}
