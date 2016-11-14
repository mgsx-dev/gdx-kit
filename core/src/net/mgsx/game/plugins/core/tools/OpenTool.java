package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.Storage;
import net.mgsx.game.core.tools.Tool;

public class OpenTool extends Tool
{

	public OpenTool(EditorScreen editor) {
		super("Open", editor);
	}
	
	@Override
	protected void activate() {
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				Storage.load(editor.entityEngine, file, editor.assets, editor.serializers);
				// TODO ? rebuild();
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
