package net.mgsx.game.example;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.ui.CustomUIPlugin;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.kit.files.DesktopNativeInterface;
import net.mgsx.kit.launcher.DesktopApplication;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;
import net.mgsx.pd.audio.PdAudioNone;

/**
 * Desktop launcher for level editor
 * @author mgsx
 *
 */
public class CoreEditorDesktopLauncher {

	@PluginDef(dependencies={CustomUIPlugin.class})
	private static class EmptyPlugin extends EditorPlugin implements DefaultEditorPlugin
	{
		@Override
		public void initialize(EditorScreen editor) {
		}
	}
	
	public static void main (String[] args) 
	{
		ClassRegistry.instance = new ReflectionClassRegistry(
				ReflectionClassRegistry.kitCore,
				ReflectionClassRegistry.kitPlugins,
				ReflectionClassRegistry.behaviorTree,
				"net.mgsx.game.examples.ui"
				);
		
		DesktopNativeInterface nativeService = new DesktopNativeInterface(); 
		NativeService.instance = nativeService; 
		
		// set no Pd audio by default to allow running Pd and game at the same time.
		// all real audio implementations (java or openAL) will lock audio.
		Pd.audio = new PdAudioNone();
		LwjglApplicationConfiguration.disableAudio = true;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new EmptyPlugin());
		editConfig.autoSavePath = null;
		
		final EditorApplication editor = new EditorApplication(editConfig){
			@Override
			public void create() {
				Pd.audio.create(new PdConfiguration());
				super.create();
			}
		};
		
		new DesktopApplication(editor, config);
	}
}
