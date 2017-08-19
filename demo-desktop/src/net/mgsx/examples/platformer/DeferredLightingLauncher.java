package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.plugins.DefaultEditorPlugin;
import net.mgsx.game.plugins.g3d.G3DPlugin;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.kit.launcher.DesktopApplication;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

/**
 * Desktop launcher for level editor
 * @author mgsx
 *
 */
public class DeferredLightingLauncher {

	public static class EmptyPlugin extends EditorPlugin implements DefaultEditorPlugin{
		@Override
		public void initialize(EditorScreen editor) {
		}
	}
	public static void main (String[] args) 
	{
		ClassRegistry.instance = new ReflectionClassRegistry();
		
		PdConfiguration.disabled = true;
		
		LwjglApplicationConfiguration.disableAudio = true;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		G3DPlugin.defferedRendering = true;
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new EmptyPlugin());
		editConfig.path = args.length > 0 ? args[0] : null;
		
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
