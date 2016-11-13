package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DialogCallback;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.tools.Tool;

public class SaveTool extends Tool
{

	public SaveTool(EditorScreen editor) {
		super("Save", editor);
	}
	
	@Override
	protected void activate() {
		NativeService.instance.openSaveDialog(new DialogCallback() {
			@Override
			public void selected(FileHandle file) {
				Storage.save(editor.entityEngine, editor.assets, file, true, editor.serializers); // TODO pretty configurable
			}
			@Override
			public void cancel() {
			}
		});
		end();
	}

}
