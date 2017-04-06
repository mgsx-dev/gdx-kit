package net.mgsx.game.plugins.editor.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.components.Repository;
import net.mgsx.game.core.plugins.SelectorPlugin;

public class SelectionSystem extends EntitySystem
{
	final public Array<Entity> selection = new Array<Entity>();
	public boolean isDirty = true;
	
	public Array<SelectorPlugin> selectors = new Array<SelectorPlugin>();

	
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
	
	/**
	 * return last selected entity or null if selection is empty.
	 * @return
	 */
	public Entity selected() 
	{
		return selection.size > 0 ? last() : null;
	}
	
	/**
	 * get current entity which can be the selected entity (last in selection)
	 * or a fresh new one.
	 * Note that entity will have repository component which mark it as persistable.
	 * use {@link #transcientEntity()} to create a non persistable entity.
	 * @return
	 */
	public Entity currentEntity() 
	{
		if(isEmpty()){
			Entity entity = getEngine().createEntity();
			entity.add(getEngine().createComponent(Repository.class));
			getEngine().addEntity(entity);
			return entity;
		}
		return last();
	}
	
	public Entity transcientEntity(){
		if(isEmpty()){
			return getEngine().createEntity();
		}
		Entity entity = last();
		if(Repository.components.has(entity)){
			return getEngine().createEntity();
		}
		return entity;
	}

	public void set(Entity entity) 
	{
		selection.clear();
		selection.add(entity);
		invalidate();
	}
	
}
