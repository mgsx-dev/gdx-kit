package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.tools.Tool;

public class SaveTool extends Tool
{

	public SaveTool(EditorScreen editor) {
		super("Save", editor);
	}
	
	@Override
	protected void activate() {
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				Storage.save(editor.entityEngine, editor.assets, file, true, editor.serializers); // TODO pretty configurable
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("json");
			}
			@Override
			public String description() {
				return "Patch files (json)";
			}
		});
		end();
	}

}
