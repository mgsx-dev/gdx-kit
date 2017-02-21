package net.mgsx.game.plugins.editor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;

public class SelectionSystem extends EntitySystem
{
	final public Array<Entity> selection = new Array<Entity>();
	public boolean isDirty = true;
	
	public boolean contains(Entity entity) {
		return selection.contains(entity, true);
	}

	public void remove(Entity entity) {
		selection.removeValue(entity, true);
		invalidate();
	}

	/**
	 * mark selection dirty
	 */
	public void invalidate(){
		isDirty = true;
	}

	/**
	 * check and clear dirty status
	 * @return true if selection was dirty, false otherwise.
	 */
	public boolean validate() {
		if(isDirty){
			isDirty = false;
			return true;
		}
		return false;
	}

	public int size() {
		return selection.size;
	}

	public Entity last() {
		return selection.peek();
	}

	public boolean isEmpty() {
		return selection.size == 0;
	}

	public void clear() {
		selection.clear();
		invalidate();
	}

	public void add(Entity entity) {
		selection.add(entity);
		invalidate();
	}

	public Entity last(int i) {
		return selection.get(selection.size - i - 1);
	}

	public void addAll(Array<Entity> entities) 
	{
		selection.addAll(entities);
		invalidate();
	}
	
}
