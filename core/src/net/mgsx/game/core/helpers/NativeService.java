package net.mgsx.game.core.helpers;

import com.badlogic.gdx.files.FileHandle;

public class NativeService {

	public static NativeServiceInterface instance;
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
}
