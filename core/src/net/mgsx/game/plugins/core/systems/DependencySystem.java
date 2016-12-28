package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.GamePipeline;

public class DependencySystem extends EntitySystem
{
	private static class EntityLink{
		final public Array<Entity> parents = new Array<Entity>();
		final public Array<Entity> children = new Array<Entity>();
	}
	private ObjectMap<Entity, EntityLink> dependencies = new ObjectMap<Entity, EntityLink>();
	
	public DependencySystem() {
		super(GamePipeline.AFTER_LOGIC);
	}
	
	public void link(Entity parent, Entity child){
		EntityLink parentDeps = dependencies.get(parent);
		if(parentDeps == null) dependencies.put(parent, parentDeps = new EntityLink());
		
		EntityLink childDeps = dependencies.get(child);
		if(childDeps == null) dependencies.put(child, childDeps = new EntityLink());
		
		parentDeps.children.add(child);
		childDeps.parents.add(parent);
	}
	
	public Array<Entity> unlinkChildren(Entity entity) {
		EntityLink deps = dependencies.get(entity);
		Array<Entity> children = new Array<Entity>();
		if(deps != null)
		{
			for(Entity child : deps.children){
				children.add(child);
				unlinkParents(child);
			}
			deps.children.clear();
		}
		return children;
	}
	public void unlinkParents(Entity entity) {
		EntityLink deps = dependencies.get(entity);
		if(deps != null)
		{
			deps.parents.clear();
		}
	}
	
	public void removeChildren(Entity entity) {
		EntityLink deps = dependencies.get(entity);
		if(deps != null)
		{
			for(Entity child : deps.children) getEngine().removeEntity(child);
			deps.children.clear();
		}
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		engine.addEntityListener(new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				EntityLink deps = dependencies.get(entity);
				if(deps != null)
				{
					for(Entity child : deps.children) getEngine().removeEntity(child);
					dependencies.remove(entity);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		// TODO engine.removeEntityListener(listener);
		super.removedFromEngine(engine);
	}

	

	
}
