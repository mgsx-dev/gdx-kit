package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.components.EntityEmitter;

public class EntityEmitterTool extends Tool
{

	public EntityEmitterTool(EditorScreen editor) {
		super("Entity Emitter", editor);
	}
	
	@Override
	protected void activate() {
		
		NativeService.instance.openSaveDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) 
			{
				LoadConfiguration config = new LoadConfiguration();
				config.assets = editor.assets;
				config.registry = editor.registry;
				config.engine = editor.entityEngine;
				
				Entity master = editor.currentEntity();
				EntityEmitter emitter = getEngine().createComponent(EntityEmitter.class);
				emitter.template = EntityGroupStorage.loadNow(file.path(), config);
				master.add(emitter);
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
