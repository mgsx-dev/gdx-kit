package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.Tool;

@Editable
public class OpenTool extends Tool
{
	public OpenTool(EditorScreen editor) {
		super("Open", editor);
	}
	@Editable("Load All")
	public void loadAll(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				LoadConfiguration config = new LoadConfiguration();
				config.assets = editor.assets;
				config.engine = editor.entityEngine;
				config.registry = editor.registry;
				
				config.loadEntities = true;
				config.loadSettings = true;
				config.loadViews = true;
				
				EntityGroupStorage.loadForEditing(editor, file.path(), config);
				end();
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
	}
	@Editable("Load Entities")
	public void loadEntities(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				LoadConfiguration config = new LoadConfiguration();
				config.assets = editor.assets;
				config.engine = editor.entityEngine;
				config.registry = editor.registry;
				
				EntityGroupStorage.loadForEditing(editor, file.path(), config);
				end();
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
	}
	@Editable("Load Settings")
	public void loadSettings(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				LoadConfiguration config = new LoadConfiguration();
				config.assets = editor.assets;
				config.engine = editor.entityEngine;
				config.registry = editor.registry;
				
				config.loadEntities = false;
				config.loadSettings = true;
				config.loadViews = true;
				
				EntityGroupStorage.loadForEditing(editor, file.path(), config);
				end();
				
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("json");
			}
			@Override
			public String description() {
				return "Settings files (json)";
			}
		});
	}
	

}
