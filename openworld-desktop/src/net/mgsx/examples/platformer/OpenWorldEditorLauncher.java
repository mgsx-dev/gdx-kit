package net.mgsx.examples.platformer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.examples.openworld.OpenWorldEditorPlugin;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.LocalGameStorage;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.kit.launcher.DesktopApplication;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

/**
 * Desktop launcher for level editor
 * @author mgsx
 *
 */
public class OpenWorldEditorLauncher {

	private final static boolean useRemoteCameraMatrix = false;
	
	public static void main (String[] args) 
	{
		ClassRegistry.instance = new ReflectionClassRegistry(
				"net.mgsx.game.examples.openworld"
				);
		
		PdConfiguration.remoteEnabled = true;
//		PdConfiguration.remoteHost = "192.168.0.34";
		
		LwjglApplicationConfiguration.disableAudio = true;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new OpenWorldEditorPlugin());
		editConfig.path = args.length > 0 ? args[0] : null;
		
		final EditorApplication editor = new EditorApplication(editConfig){
			@Override
			public void create() {
				Gdx.app.setLogLevel(Application.LOG_DEBUG);
				
				// Wrap with local service and disable cloud storage
				GAPI.service = new LocalGameStorage(GAPI.service, false);
				GAPI.service.init("OpenWorld");
				
				// TODO should be an asset ...
				OpenWorldModel.load();
				
				Pd.audio.create(new PdConfiguration());
				if(useRemoteCameraMatrix){
					OpenWorldCameraSystem.cameraMatrixProvider = new CameraMatrixProviderRemote();
				}
				super.create();
			}
		};
		
		new DesktopApplication(editor, config);
	}
}
