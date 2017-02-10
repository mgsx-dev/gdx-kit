package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.examples.platformer.PlatformerEditorPlugin;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.kit.files.DesktopNativeInterface;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.audio.PdAudioNone;

/**
 * Desktop launcher for level editor
 * @author mgsx
 *
 */
public class PlatformerLevelEditorLauncher {

	public static void main (String[] args) 
	{
		ClassRegistry.instance = new ReflectionClassRegistry(
				ReflectionClassRegistry.kitCore,
				ReflectionClassRegistry.kitPlugins,
				ReflectionClassRegistry.behaviorTree,
				"net.mgsx.game.examples.platformer"
				);
		
		DesktopNativeInterface nativeService = new DesktopNativeInterface(); 
		NativeService.instance = nativeService; 
		
		// set no Pd audio by default to allow running Pd and game at the same time.
		// all real audio implementations (java or openAL) will lock audio.
		Pd.audio = new PdAudioNone();
		LwjglApplicationConfiguration.disableAudio = true;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new PlatformerEditorPlugin());
		editConfig.path = args.length > 0 ? args[0] : null;
		
		new LwjglApplication(new EditorApplication(editConfig){
			@Override
			public void create() {
				Pd.audio.create(new PdConfiguration());
				super.create();
			}
		}, config);
	}
}
