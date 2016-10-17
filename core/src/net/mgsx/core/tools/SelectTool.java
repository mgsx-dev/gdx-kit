package net.mgsx.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;
import net.mgsx.core.components.Movable;
import net.mgsx.core.plugins.SelectorPlugin;

public class SelectTool extends Tool
{
	private Array<Entity> selection = new Array<Entity>();
	protected Vector2 prev;
	protected boolean moving = false;
	
	public SelectTool(Editor editor) {
		super("Select", editor);
	}
	
	protected SelectTool(String name, Editor editor) {
		super(name, editor);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		if(button == Input.Buttons.LEFT){
			selection.clear();
			for(SelectorPlugin selector : editor.selectors){
				selector.getSelection(selection, screenX, screenY);
			}
			for(Entity entity : selection){
				handleSelection(entity, editor.selection);
			}
			if(selection.size == 0) {
				editor.selection.clear();
				editor.invalidateSelection();
			}
			moving = true;
			for(Entity entity : editor.selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.moveBegin(entity);
			}
			prev = unproject(screenX, screenY);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(moving){
			Vector3 worldPos = editor.orthographicCamera.unproject(new Vector3(screenX, screenY, 0)); // unproject(screenX, screenY);
			Vector3 delta = new Vector3(worldPos).sub(prev.x, prev.y, 0);
			if(ctrl()){
				delta.rotate(90, 1, 0, 0);
			}else if(shift()){
				delta.set(0, 0, delta.y);
			}
			for(Entity entity : editor.selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.move(entity, delta);
			}
			prev.set(worldPos.x, worldPos.y);
			return true; // catch event
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(moving){
			moving = false;
			
			for(Entity entity : editor.selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.moveEnd(entity);
			}
			
		}
		return false;
	}
	
	protected <T> void handleSelection(T itemSelected, Array<T> selection)
	{
		if(ctrl()){
			if(selection.contains(itemSelected, true)){
				selection.removeValue(itemSelected, true);
				editor.invalidateSelection();
			}else{
				selection.add(itemSelected);
				editor.invalidateSelection();
			}
		}else if(shift()){
			if(!selection.contains(itemSelected, true)){
				selection.add(itemSelected);
				editor.invalidateSelection();
			}
		}else if(!selection.contains(itemSelected, true)){
			selection.clear();
			selection.add(itemSelected);
			editor.invalidateSelection();
		}
	}

}
