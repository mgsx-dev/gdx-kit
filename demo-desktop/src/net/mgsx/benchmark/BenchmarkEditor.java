package net.mgsx.benchmark;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.plugins.camera.CameraPlugin;
import net.mgsx.kit.files.DesktopNativeInterface;

public class BenchmarkEditor {
	public static void main (String[] args) 
	{
		NativeService.instance = new DesktopNativeInterface(); 
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new CameraPlugin());
		new LwjglApplication(new EditorApplication(editConfig ), config);
	}
}