package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.tools.ClickTool;

public class CreateProxyTool extends ClickTool {
	private FileHandle file;

	public CreateProxyTool(EditorScreen editor) {
		super("Proxy", editor);
	}

	@Override
	protected void activate() {
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle selectedFile) {
				file = selectedFile;
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("json");
			}
			@Override
			public String description() {
				return "Patch files (json)";
			}
			@Override
			public void cancel() {
				end();
			}
		});
	}

	@Override
	protected void create(final Vector2 position) 
	{
		EntityGroupStorage.loadAsProxy(editor.assets, editor.registry, editor.entityEngine, file.path(), position);
	}
}