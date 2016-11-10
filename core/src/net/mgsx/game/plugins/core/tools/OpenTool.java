package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DialogCallback;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.tools.Tool;

public class OpenTool extends Tool
{

	public OpenTool(Editor editor) {
		super("Open", editor);
	}
	
	@Override
	protected void activate() {
		NativeService.instance.openSaveDialog(new DialogCallback() {
			@Override
			public void selected(FileHandle file) {
				Storage.load(editor.entityEngine, file, editor.assets, editor.serializers);
				// TODO ? rebuild();
			}
			@Override
			public void cancel() {
			}
		});
		end();
	}

}
