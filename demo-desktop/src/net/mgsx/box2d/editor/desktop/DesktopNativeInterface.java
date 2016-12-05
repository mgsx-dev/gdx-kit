package net.mgsx.box2d.editor.desktop;

import java.io.File;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.badlogic.gdx.Gdx;

import net.mgsx.game.core.helpers.NativeService.DialogCallback;
import net.mgsx.game.core.helpers.NativeService.NativeServiceInterface;

public class DesktopNativeInterface implements NativeServiceInterface
{
	// TODO replace this native interface either by attaching to LibGDX applet or pure LibGDX browser ...

	public String path = ".";
	
	private void openDialog(final DialogCallback callback, boolean save){
		JApplet applet = new JApplet(); // TODO fail safe
		final JFileChooser fc = new JFileChooser(new File(path));
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return callback.description();
			}
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || callback.match(Gdx.files.absolute(f.getAbsolutePath()));
			}
		});
		int r = save ? fc.showSaveDialog(applet) : fc.showOpenDialog(applet);
		if(r == JFileChooser.APPROVE_OPTION){
			final File file = fc.getSelectedFile();
			path = file.getParent();
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					callback.selected(Gdx.files.absolute(file.getAbsolutePath()));
				}
			});
		}else{
			callback.cancel();
		}
		applet.destroy();
	}
	@Override
	public void openSaveDialog(final DialogCallback callback) {
		openDialog(callback, true);
	}
	
	@Override
	public void openLoadDialog(final DialogCallback callback) {
		openDialog(callback, false);
	}
}