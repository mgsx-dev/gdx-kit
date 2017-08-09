package net.mgsx.game.core.helpers;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class NativeService {

	static
	{
		if(Gdx.app.getType() == ApplicationType.Desktop){
			instance = ReflectionHelper.newInstance("net.mgsx.kit.files.DesktopNativeInterface");
		}else{
			instance = new NativeServiceInterfaceDefault();
		}
	}
	
	/**
	 * native service implementation automatically injected.
	 */
	public static final NativeServiceInterface instance;
	
	public static interface DialogCallback
	{
		public boolean match(FileHandle file);
		public void cancel();
		public void selected(FileHandle file);
		public String description();
	}
	public static class DefaultCallback implements DialogCallback
	{
		@Override
		public boolean match(FileHandle file) {
			return true;
		}

		@Override
		public void cancel() {
		}

		@Override
		public void selected(FileHandle file) {
		}

		@Override
		public String description() {
			return "";
		}
	}
	public static interface NativeServiceInterface
	{
		public void openSaveDialog(DialogCallback callback);
		public void openLoadDialog(DialogCallback callback);
	}
	
	private static class NativeServiceInterfaceDefault implements NativeServiceInterface
	{
		@Override
		public void openSaveDialog(DialogCallback callback) {
			callback.cancel();
		}

		@Override
		public void openLoadDialog(DialogCallback callback) {
			callback.cancel();
		}
	}
	
}
