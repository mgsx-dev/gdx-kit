package net.mgsx.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.core.Editor;
import net.mgsx.core.plugins.Movable;

public class MoveToolBase extends Tool
{
	private Vector2 prev;
	private boolean moving = false;
	private Editor editor;
	public MoveToolBase(Editor editor) {
		super("Move", editor.orthographicCamera);
		this.editor = editor;
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
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(moving){
			Vector2 worldPos = unproject(screenX, screenY);
			Vector2 delta = new Vector2(worldPos).sub(prev);
			for(Entity entity : editor.selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.move(entity, delta);
			}
			prev = worldPos;
			return true; // catch event
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			moving = true;
			prev = unproject(screenX, screenY);
			for(Entity entity : editor.selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.moveBegin(entity);
			}
		}
		return false;
	}
}
