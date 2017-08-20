package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.commands.Command;
import net.mgsx.game.core.components.Initializable;
import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.core.tools.Tool;

public class DeleteTool extends Tool {

	public DeleteTool(EditorScreen editor) {
		super("Delete", editor);
	}

	@Override
	protected void activate() 
	{
		
		historySystem.performCommand(new Command(){
			
			// copy current selection
			final Array<Entity> entities = new Array<Entity>(selection().selection);
			
			// TODO remove components and store for futur restoration
			final Array<Array<Component>> backup = new Array<Array<Component>>();
			
			@Override
			public void commit() {
				// clear selection
				selection().clear();
				// remove netities
				for(Entity entity : entities){
					backup.add(ArrayHelper.array(entity.getComponents()));
					getEngine().removeEntity(entity);
				}
				entities.clear();
			}
			@Override
			public void rollback() 
			{
				while(backup.size > 0){
					Entity entity = getEngine().createEntity();
					for(Component component : backup.pop()){
						if(component instanceof Initializable){
							((Initializable) component).initialize(getEngine(), entity);
						}
						entity.add(component);
					}
					entities.add(entity);
					getEngine().addEntity(entity);
				}
				// restore selection
				selection().addAll(entities);
			}
		});
	}
}
