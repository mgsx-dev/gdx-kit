package net.mgsx.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.Movable;

public class MoveToolBase extends Tool
{
	private Vector2 prev;
	private boolean moving = false;
	private Editor editor;
	private Array<Entity> selection = new Array<Entity>(); // TODO no, replace these tools by a selector concept ...
	
	public MoveToolBase(Editor editor) {
		super("Move", editor.orthographicCamera);
		this.editor = editor;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(moving){
			moving = false;
			
			for(Entity entity : selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.moveEnd(entity);
			}
			selection.clear();
			
		}
		return false;
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
			for(Entity entity : selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.move(entity, delta);
			}
			prev.set(worldPos.x, worldPos.y);
			return true; // catch event
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			moving = true;
			prev = unproject(screenX, screenY);
			selection.clear();
			for(Entity entity : editor.selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.moveBegin(entity);
				selection.add(entity);
			}
		}
		return false;
	}
}
