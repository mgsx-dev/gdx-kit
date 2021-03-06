package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.ClickTool;
import net.mgsx.game.plugins.btree.BTreeModel;

@Editable
public class ImportEntitiesTool extends ClickTool 
{
	@Editable
	public boolean proxy = false;
	
	private FileHandle file;

	public ImportEntitiesTool(EditorScreen editor) {
		super("Import", editor); // TODO maybe rename Import Entities when merged in all tools ?
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
		LoadConfiguration config = new LoadConfiguration();
		config.assets = editor.assets;
		config.registry = editor.registry;
		config.engine = editor.entityEngine;
		
		if(proxy)
		{
			EntityGroupStorage.loadAsProxy(file.path(), position, config);
		}
		else
		{
			for(Entity entity : EntityGroupStorage.loadForEditing(editor, file.path(), config)){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null){
					movable.moveTo(entity, new Vector3(position.x, position.y, 0)); // sprite plan
				}
				BTreeModel btree = BTreeModel.components.get(entity);
				if(btree != null){
					btree.enabled = true;
					btree.remove = true;
				}
			}
		}
	}
}