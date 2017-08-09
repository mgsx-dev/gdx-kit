package net.mgsx.benchmark;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.mgsx.game.core.EditorApplication;
import net.mgsx.game.core.EditorConfiguration;
import net.mgsx.game.plugins.camera.CameraPlugin;

public class BenchmarkEditor {
	public static void main (String[] args) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		EditorConfiguration editConfig = new EditorConfiguration();
		editConfig.plugins.add(new CameraPlugin());
		new LwjglApplication(new EditorApplication(editConfig ), config);
	}
}