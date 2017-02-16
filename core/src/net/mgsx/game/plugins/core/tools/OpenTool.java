package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.Tool;

// TODO just one load action + config like SaveTool
@Editable
public class OpenTool extends Tool
{
	@Editable
	public boolean systems = true;

	@Editable
	public boolean views = true;

	@Editable
	public boolean entities = true;
	
	public OpenTool(EditorScreen editor) {
		super("Open", editor);
	}
	@Editable("Browse...")
	public void loadAll(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				LoadConfiguration config = new LoadConfiguration();
				config.assets = editor.assets;
				config.engine = editor.entityEngine;
				config.registry = editor.registry;
				
				config.loadEntities = entities;
				config.loadSettings = systems;
				config.loadViews = views;
				
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

}
