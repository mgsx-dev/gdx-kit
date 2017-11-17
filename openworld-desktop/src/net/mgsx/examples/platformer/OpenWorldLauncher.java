package net.mgsx.examples.platformer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GameApplication;
import net.mgsx.game.core.GameRegistry;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.meta.ClassRegistry;
import net.mgsx.game.examples.openworld.OpenWorldPlugin;
import net.mgsx.game.examples.openworld.model.OpenWorldModel;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldHUDSystem;
import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.LocalGameStorage;
import net.mgsx.kit.config.ReflectionClassRegistry;
import net.mgsx.kit.launcher.DesktopApplication;
import net.mgsx.pd.Pd;
import net.mgsx.pd.PdConfiguration;

/**
 * @author mgsx
 * TODO rename demo ...
 */
public class OpenWorldLauncher {

	public static void main (String[] args) 
	{
		// some patch for demo mode
		OpenWorldHUDSystem.DISPLAY_MAP_ONLY = true;
		OpenWorldCameraSystem.ROTATE_MOUSE_BUTTON = Input.Buttons.LEFT;
		
		ClassRegistry.instance = new ReflectionClassRegistry(
				"net.mgsx.game.examples.openworld"
				);
		
		// TODO parse config file if any ... to :
		// - quick change network settings
		// - quick disable some greedy features (force lowQuality ...)
		final Properties props = new Properties();
		File configFile = new File("config.properties");
		if(configFile.exists()){
			try {
				props.load(new FileInputStream(configFile));
			} catch (Exception e) {
				throw new GdxRuntimeException(e);
			}
		}
		PdConfiguration.remoteHost = props.getProperty("pd.remote.host", "localhost");
		PdConfiguration.remoteSendPort = Integer.parseInt(props.getProperty("pd.remote.send.port", "3000"));
		
		PdConfiguration.remoteEnabled = true;
		
		
		
		LwjglApplicationConfiguration.disableAudio = true;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		final GameApplication game = new GameApplication() {
			@Override
			public void create() {
				
				super.create();
				
				// TODO ...
				GAPI.service = new LocalGameStorage(GAPI.service, false);
				GAPI.service.init("OpenWorld");
				
				OpenWorldModel.load();
				
				Pd.audio.create(new PdConfiguration());
				
				GameRegistry registry = new GameRegistry();
				registry.registerPlugin(new OpenWorldPlugin());
				GameScreen screen = new GameScreen(this, getAssets(), registry);
				
				screen.load(Gdx.files.internal("openworld/openworld-scene5.json"));
				
				assets.finishLoading();
				
				setScreen(screen);
				
			}
		};
		
		new DesktopApplication(game, config);
	}
}
