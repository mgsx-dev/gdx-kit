package net.mgsx.game.plugins.ashley.editors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;

public class AshleyEntitiesEditor implements GlobalEditorPlugin
{
	private static class EntityItem{
		Entity entity;
		int index;
		
		public EntityItem(Entity entity, int index) {
			super();
			this.entity = entity;
			this.index = index;
		}

		@Override
		public String toString() {
			return "Entity # " + String.valueOf(index);
		}
	}

	@Override
	public Actor createEditor(final Editor editor, Skin skin) 
	{
		final ObjectMap<Entity, EntityItem> map = new ObjectMap<Entity, AshleyEntitiesEditor.EntityItem>();
		
		final Array<EntityItem> items = new Array<EntityItem>();

		final List<EntityItem> selector = new List<EntityItem>(skin);
		
		editor.entityEngine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				EntityItem item = map.remove(entity);
				items.removeValue(item, true);
				selector.setItems(items);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				EntityItem item = new EntityItem(entity, editor.entityEngine.getEntities().indexOf(entity, true));
				map.put(entity, item);
				items.add(item);
				selector.setItems(items);
			}
		});
		
		
		for(int i=0 ; i<editor.entityEngine.getEntities().size() ; i++){
			Entity entity = editor.entityEngine.getEntities().get(i);
			EntityItem item = new EntityItem(entity, i);
			map.put(entity, item);
			items.add(item);
		}
		
		selector.setItems(items);
		
		selector.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				editor.selection.clear();
				if(selector.getSelected() != null){
					editor.selection.add(selector.getSelected().entity);
				}
				editor.invalidateSelection();
			}
		});
		
		selector.addListener(new InputListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				selector.getStage().setKeyboardFocus(selector);
				return super.touchDown(event, x, y, pointer, button);
			}
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				int index = selector.getSelectedIndex();
				if(keycode == Input.Keys.UP && index > 0){
					selector.setSelectedIndex(index - 1); 
					return true;
				}
				else if(keycode == Input.Keys.DOWN && index < selector.getItems().size-1){
					selector.setSelectedIndex(index + 1); 
					return true;
				}
				return super.keyDown(event, keycode);
			}
		});
		
		return new ScrollPane(selector, skin);
	}

}
