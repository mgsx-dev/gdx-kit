package net.mgsx.examples.platformer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.examples.lsystem.LSystemEditorPlugin;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

/**
 * Desktop launcher for level editor
 * @author mgsx
 *
 */
public class LSystemEditorLauncher {

	public static void main (String[] args) 
	{
		ClassRegistry.instance = new ReflectionClassRegistry(
				ReflectionClassRegistry.kitCore,
				ReflectionClassRegistry.kitCorePlugin,
				"net.mgsx.game.examples.lsystem"
				);
		
		PdConfiguration.disabled = true;
		LwjglApplicationConfiguration.disableAudio = true;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new LSystemEditorPlugin());
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
