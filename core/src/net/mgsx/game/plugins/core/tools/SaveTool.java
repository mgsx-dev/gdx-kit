package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.SaveConfiguration;
import net.mgsx.game.core.tools.Tool;

@Editable
public class SaveTool extends Tool
{

	@Editable(doc="Make all paths relative to asset directory")
	public boolean stripPaths = true;
	
	@Editable(doc="JSON pretty format")
	public boolean pretty = true;
	
	public SaveTool(EditorScreen editor) {
		super("Save", editor);
	}
	
	@Editable("Save As...")
	public void save(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				SaveConfiguration config = new SaveConfiguration();
				config.assets = editor.assets;
				config.engine = editor.entityEngine;
				config.registry = editor.registry;
				
				config.pretty = pretty;
				config.stripPaths = stripPaths;
				
				EntityGroupStorage.save(file, config);
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
