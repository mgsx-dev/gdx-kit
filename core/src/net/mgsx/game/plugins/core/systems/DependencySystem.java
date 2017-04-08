package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.core.components.DependencyComponent;

/**
 * User code can register listeners on entities in order to get warn when
 * this entity is removed.
 * 
 * @author mgsx
 *
 */
public class DependencySystem extends EntitySystem
{
	public static interface LinkListener{
		public void onUnlink(Entity source, Entity target);
	}
	private static class EntityLink{
		final public ObjectMap<Entity, Array<LinkListener>> listeners = new ObjectMap<Entity, Array<LinkListener>>();
	}
	private ObjectMap<Entity, EntityLink> dependencies = new ObjectMap<Entity, EntityLink>();
	
	private EntityListener listener = new EntityListener() {
		@Override
		public void entityRemoved(Entity entity) {
			EntityLink deps = dependencies.get(entity);
			for(Entry<Entity, Array<LinkListener>> entry : deps.listeners){
				for(LinkListener listener : entry.value){
					listener.onUnlink(entry.key, entity);
				}
				entry.value.clear();
			}
			deps.listeners.clear();
			dependencies.remove(entity);
		}
		
		@Override
		public void entityAdded(Entity entity) {
		}
	};
	
	public DependencySystem() {
		super(GamePipeline.AFTER_LOGIC);
	}
	
	/**
	 * Hard composite link (Default behavior) : source is removed when target is removed
	 * @param parent the dependency target
	 * @param child the dependant entity
	 */
	public void link(Entity parent, Entity child){
		
		link(child, parent, new LinkListener() {
			@Override
			public void onUnlink(Entity source, Entity target) {
				getEngine().removeEntity(source);
			}
		});
	}
	
	/**
	 * Reference link : source is processed by listener (custom behavior) when target is removed.
	 * @param source
	 * @param target
	 * @param listener
	 */
	public void link(Entity source, Entity target, LinkListener listener){
		
		if(!DependencyComponent.components.has(target)){
			target.add(getEngine().createComponent(DependencyComponent.class));
		}
		
		EntityLink targetDeps = dependencies.get(target);
		if(targetDeps == null) dependencies.put(target, targetDeps = new EntityLink());
		
		Array<LinkListener> listeners = targetDeps.listeners.get(target);
		if(listeners == null) targetDeps.listeners.put(source, listeners = new Array<LinkListener>());
		listeners.add(listener);
	}
	
	/**
	 * remove link from children to parent
	 * @param parent
	 * @return the unlinked children
	 */
	public Array<Entity> unlinkChildren(Entity parent) {
		EntityLink deps = dependencies.get(parent);
		Array<Entity> children = new Array<Entity>();
		if(deps != null)
		{
			for(Entry<Entity, Array<LinkListener>> entry : deps.listeners){
				children.add(entry.key);
				entry.value.clear();
			}
			deps.listeners.clear();
		}
		return children;
	}
	
	/**
	 * remove children entity from engine
	 * @param entity
	 */
	public void removeChildren(Entity entity) {
		for(Entity child : unlinkChildren(entity)){
			getEngine().removeEntity(child);
		}
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(DependencyComponent.class).get(), listener);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(listener);
		super.removedFromEngine(engine);
	}

	

	
}
