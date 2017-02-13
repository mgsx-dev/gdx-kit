package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EngineStorage;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.Tool;

@Editable
public class OpenTool extends Tool
{
	public OpenTool(EditorScreen editor) {
		super("Open", editor);
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
				
				EntityGroupStorage.loadForEditing(file.path(), config);
				end();
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
				
				EngineStorage.load(file, config);
				end();
				// TODO ? rebuild();
				
				// TODO if option load views is checked !
				editor.pinEditors(config.visibleSystems);
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
