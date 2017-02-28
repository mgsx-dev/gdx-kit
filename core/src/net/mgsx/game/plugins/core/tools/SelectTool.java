package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.plugins.SelectorPlugin;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class SelectTool extends Tool
{
	private Array<Entity> currentSelection = new Array<Entity>();
	protected Vector2 prev;
	protected boolean moving = false;
	
	private Vector2 ghostPosition = new Vector2();
	private Vector2 snapPosition = new Vector2();
	private Vector2 prevSnapPosition = new Vector2();
	
	public SelectTool(EditorScreen editor) {
		super("Select", editor);
	}
	
	protected SelectTool(String name, EditorScreen editor) {
		super(name, editor);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		if(button == Input.Buttons.LEFT){
			currentSelection.clear();
			for(SelectorPlugin selector : selection().selectors){
				selector.getSelection(currentSelection, screenX, screenY);
			}
			// TODO select by knot only if not in selection ?
			// then add selection for movables : TODO use engine query instead
			// TODO make selector instead !
			for(Entity entity : editor.entityEngine.getEntities()){
				Vector3 pos = new Vector3();
				Movable movable = entity.getComponent(Movable.class);
				Transform2DComponent transform = Transform2DComponent.components.get(entity);
				if(movable != null){
					movable.getPosition(entity, pos);
				}
				else if(transform != null){
					pos.set(transform.position.x, transform.position.y, 0);
				}
				else continue;
				Vector2 s = new Vector2(5, 5); // size of displayed knot !
				Vector3 v = editor.getGameCamera().project(pos);
				if(new Rectangle(v.x-s.x, v.y-s.y, 2*s.x, 2*s.y).contains(screenX, Gdx.graphics.getHeight() - screenY)){
					currentSelection.add(entity);
				}
			}
			
			
			for(Entity entity : currentSelection){
				handleSelection(entity, selection().selection);
			}
			if(currentSelection.size == 0) {
				selection().clear();
			}
			for(Entity entity : selection().selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null) movable.moveBegin(entity);
			}
			ghostPosition = unproject(screenX, screenY);
			prevSnapPosition.set(ghostPosition);
			prev = new Vector2(screenX, screenY);
			return moving = currentSelection.size > 0;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(moving){
			Vector3 worldPos = new Vector3(screenX, screenY, 0); // editor.perspectiveCamera.unproject(new Vector3(screenX, screenY, 0)); // unproject(screenX, screenY);
			Vector2 pixel = pixelSize();
			Vector3 delta = new Vector3(worldPos).sub(prev.x, prev.y, 0).scl(pixel.x, pixel.y, 1); // XXX 10f pixel screen
			delta.z = 0; // XXX better fix by using vector 3 for prev as well ?
//			if(ctrl()){
//				delta.rotate(90, 1, 0, 0);
//			}else if(shift()){
//				delta.set(0, 0, delta.y);
//			}
			
			ghostPosition.add(delta.x, delta.y);
			
			snap(snapPosition.set(ghostPosition));
			
			delta.set(snapPosition, 0).sub(prevSnapPosition.x, prevSnapPosition.y, 0);
			
			for(Entity entity : selection().selection){
				Movable movable = entity.getComponent(Movable.class);
				if(movable != null){
					movable.move(entity, delta);
				}
				else{
					Transform2DComponent transform = Transform2DComponent.components.get(entity);
					if(transform != null){
						transform.position.add(delta.x, delta.y);
					}
				}
			}
			prev.set(worldPos.x, worldPos.y);
			prevSnapPosition.set(snapPosition);
			return true; // catch event
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer,
			int button) {
		if(moving){
			moving = false;
			
			for(Entity entity : selection().selection){
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
				selection().invalidate();
			}else{
				selection.add(itemSelected);
				selection().invalidate();
			}
		}else if(shift()){
			if(!selection.contains(itemSelected, true)){
				selection.add(itemSelected);
				selection().invalidate();
			}
		}else if(!selection.contains(itemSelected, true)){
			selection.clear();
			selection.add(itemSelected);
			selection().invalidate();
		}
	}

}
