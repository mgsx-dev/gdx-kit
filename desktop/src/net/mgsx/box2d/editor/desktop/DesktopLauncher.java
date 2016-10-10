package net.mgsx.box2d.editor.desktop;

import java.io.File;

import javax.swing.JApplet;
import javax.swing.JFileChooser;

import net.mgsx.box2d.editor.Box2DEditor;
import net.mgsx.fwk.editor.NativeService;
import net.mgsx.fwk.editor.NativeService.DialogCallback;
import net.mgsx.fwk.editor.NativeService.NativeServiceInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		NativeService.instance = new NativeServiceInterface() {
			
			@Override
			public void openSaveDialog(final DialogCallback callback) {
				JApplet applet = new JApplet();
				final JFileChooser fc = new JFileChooser();
				int r = fc.showSaveDialog(applet);
				final File file = fc.getSelectedFile();
				Gdx.app.postRunnable(new Runnable() {
					
					@Override
					public void run() {
						callback.selected(Gdx.files.absolute(file.getAbsolutePath()));
					}
				});
				applet.destroy();
			}
			
			@Override
			public void openLoadDialog(final DialogCallback callback) {
				JApplet applet = new JApplet();
				final JFileChooser fc = new JFileChooser();
				int r = fc.showOpenDialog(applet);
				final File file = fc.getSelectedFile();
				Gdx.app.postRunnable(new Runnable() {
					
					@Override
					public void run() {
						callback.selected(Gdx.files.absolute(file.getAbsolutePath()));
					}
				});
				applet.destroy();
			}
		};
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Box2DEditor(), config);
		
		
		
		
	}
}
