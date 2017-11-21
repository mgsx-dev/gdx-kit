package net.mgsx.examples.platformer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.badlogic.gdx.Application;
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
import net.mgsx.game.examples.openworld.model.OpenWorldRuntimeSettings;
import net.mgsx.game.examples.openworld.model.SpawnGenerator;
import net.mgsx.game.examples.openworld.systems.OpenWorldCameraSystem;
import net.mgsx.game.examples.openworld.systems.OpenWorldHUDSystem;
import net.mgsx.game.services.gapi.GAPI;
import net.mgsx.game.services.gapi.GAPIServiceStub;
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
		
		String gpuQuality = props.getProperty("gpu.quality", "auto");
		if("auto".equals(gpuQuality)){
			OpenWorldRuntimeSettings.autoQuality = true;
		}else if("high".equals(gpuQuality)){
			OpenWorldRuntimeSettings.autoQuality = false;
			OpenWorldRuntimeSettings.highQuality = true;
		}else if("low".equals(gpuQuality)){
			OpenWorldRuntimeSettings.autoQuality = false;
			OpenWorldRuntimeSettings.highQuality = false;
		}else{
			System.err.println("ignored unknown gpu.quality setting : [" + gpuQuality + "], auto mode will be used");
		}
		
		OpenWorldCameraSystem.KEY_FORWARD 	= convertKey(props.getProperty("keys.forward"), 	Input.Keys.Z);
		OpenWorldCameraSystem.KEY_BACKWARD 	= convertKey(props.getProperty("keys.backward"), 	Input.Keys.S);
		OpenWorldCameraSystem.KEY_LEFT 		= convertKey(props.getProperty("keys.left"), 		Input.Keys.Q);
		OpenWorldCameraSystem.KEY_RIGHT 	= convertKey(props.getProperty("keys.right"), 		Input.Keys.D);

		OpenWorldHUDSystem.TOGGLE_KEY 		= convertKey(props.getProperty("keys.map"), 		Input.Keys.SPACE);
		
		
		SpawnGenerator.ENABLE_AIR = true;
		SpawnGenerator.ENABLE_AQUA = false;
		SpawnGenerator.ENABLE_LAND = false;
		
		
		LwjglApplicationConfiguration.disableAudio = true;

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		final GameApplication game = new GameApplication() {
			@Override
			public void create() {
				
				Gdx.app.setLogLevel(Application.LOG_INFO);
				
				super.create();

				GAPI.service = new GAPIServiceStub();
				
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
	
	private static final int convertKey(String stringValue, int defaultValue){
		int key = defaultValue;
		if(stringValue != null){
			key = Input.Keys.valueOf(stringValue);
			if(key < 0){
				System.err.println("ignored unknown key [" + stringValue + "] default key will be used.");
				key = defaultValue;
			}
		}
		return key;
	}
}
