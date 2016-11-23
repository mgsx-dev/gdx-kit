package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.tools.ClickTool;

public class ImportPatchTool extends ClickTool {
	private FileHandle file;

	public ImportPatchTool(EditorScreen editor) {
		super("Import", editor);
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
		});
	}

	@Override
	protected void create(final Vector2 position) 
	{
		for(Entity entity : EntityGroupStorage.loadForEditing(editor.assets, editor.registry, editor.entityEngine, file.path())){
			Movable movable = entity.getComponent(Movable.class);
			if(movable != null){
				movable.move(entity, new Vector3(position.x, position.y, 0)); // sprite plan
			}
		}
		// TODO update things in GUI ?
		
	}
}