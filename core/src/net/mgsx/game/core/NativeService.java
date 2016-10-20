package net.mgsx.game.core;

import com.badlogic.gdx.files.FileHandle;

public class NativeService {

	public static NativeServiceInterface instance;
	public static interface DialogCallback
	{
		public void cancel();
		public void selected(FileHandle file);
	}
	public static interface NativeServiceInterface
	{
		public void openSaveDialog(DialogCallback callback);
		public void openLoadDialog(DialogCallback callback);
	}
}
