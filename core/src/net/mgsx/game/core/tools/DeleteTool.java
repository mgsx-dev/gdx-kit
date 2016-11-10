package net.mgsx.game.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.commands.Command;
import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.core.plugins.Initializable;

public class DeleteTool extends Tool {

	public DeleteTool(String name, Editor editor) {
		super(name, editor);
	}

	@Override
	protected void activate() 
	{
		
		editor.history.add(new Command(){
			
			// copy current selection
			final Array<Entity> entities = new Array<Entity>(editor.selection);
			
			// TODO remove components and store for futur restoration
			final Array<Array<Component>> backup = new Array<Array<Component>>();
			
			@Override
			public void commit() {
				// clear selection
				editor.selection.clear();
				editor.invalidateSelection();
				// remove netities
				for(Entity entity : entities){
					backup.add(ArrayHelper.array(entity.getComponents()));
					editor.entityEngine.removeEntity(entity);
				}
				entities.clear();
			}
			@Override
			public void rollback() 
			{
				while(backup.size > 0){
					Entity entity = editor.entityEngine.createEntity();
					for(Component component : backup.pop()){
						if(component instanceof Initializable){
							((Initializable) component).initialize(editor.entityEngine, entity);
						}
						entity.add(component);
					}
					entities.add(entity);
					editor.entityEngine.addEntity(entity);
				}
				// restore selection
				editor.selection.addAll(entities);
				editor.invalidateSelection();
			}
		});
	}
}
