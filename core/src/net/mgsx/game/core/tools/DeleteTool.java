package net.mgsx.game.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.Editor.EditorEntity;
import net.mgsx.game.core.commands.Command;
import net.mgsx.game.core.helpers.ComponentFactory;

public class DeleteTool extends Tool {

	public DeleteTool(String name, Editor editor) {
		super(name, editor);
	}

	@Override
	protected void activate() 
	{
		// copy current selection
		final Array<Entity> entities = new Array<Entity>(editor.selection);
		
		// copy configs
		final Array<EditorEntity> configs = new Array<EditorEntity>();
		for(Entity entity : editor.selection) configs.add(entity.getComponent(EditorEntity.class));
		
		editor.history.add(new Command(){
			@Override
			public void commit() {
				// clear selection
				editor.selection.clear();
				editor.invalidateSelection();
				// remove netities
				for(Entity entity : entities){
					editor.entityEngine.removeEntity(entity);
					entity = null;
				}
				entities.clear();
			}
			@Override
			public void rollback() 
			{
				// recreate entities
				for(EditorEntity config : configs){
					Entity entity = editor.createEntity();
					editor.entityEngine.addEntity(entity);
					entity.add(config);
					for(ComponentFactory factory : config.factories){
						entity.add(factory.create(entity));
					}
					entities.add(entity);
				}
				// restore selection
				editor.selection.addAll(entities);
				editor.invalidateSelection();
			}
		});
	}
}
