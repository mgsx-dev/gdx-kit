package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EngineStorage;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.SaveConfiguration;
import net.mgsx.game.core.storage.SaveConfiguration.Message;
import net.mgsx.game.core.tools.Tool;

@Editable
public class SaveTool extends Tool
{

	@Editable(doc="Make all paths relative to asset directory")
	public boolean stripPaths = true;
	
	@Editable(doc="JSON pretty format")
	public boolean pretty = true;
	
	@Editable()
	public boolean selection = false;
	
	private SaveConfiguration config;
	
	public SaveTool(EditorScreen editor) {
		super("Save", editor);
	}
	
	@Editable("Diagnostic")
	public void printDiagnostic(){
		if(config != null){
			for(Message m : config.messages){
				Gdx.app.log(m.tag, m.description);
				if(m.fullDescription != null){
					Gdx.app.error(m.tag, m.fullDescription);
				}
			}
		}
		
	}
	@Editable("Save Entities...")
	public void saveEntities(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				config = new SaveConfiguration();
				config.assets = editor.assets;
				config.engine = editor.entityEngine;
				config.registry = editor.registry;
				
				config.pretty = pretty;
				config.stripPaths = stripPaths;
				
				if(selection){
					config.entities = new Array<Entity>(editor.selection);
				}
				EntityGroupStorage.save(file, config);
				
				printDiagnostic();
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
	
	@Editable("Save Settings...")
	public void saveSettings(){
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				config = new SaveConfiguration();
				config.assets = editor.assets;
				config.engine = editor.entityEngine;
				config.registry = editor.registry;
				
				config.pretty = pretty;
				config.stripPaths = stripPaths;
				
				EngineStorage.save(file, config);
				
				printDiagnostic();
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
