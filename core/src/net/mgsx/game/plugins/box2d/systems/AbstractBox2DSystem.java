package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.FamilyBuilder;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;

abstract public class AbstractBox2DSystem extends EntitySystem
{
	final private EntityListener listener = new EntityListener() {
		
		@Override
		public void entityRemoved(Entity entity) {
			// if box2d objects doesn't exists, there is no need to unregister listeners.
			Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
			if(physics != null && physics.body != null){
				unregisterListener(entity, physics);
			}
		}
		
		@Override
		public void entityAdded(Entity entity) {
			Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
			if(physics != null && physics.body != null){
				registerListener(entity, physics);
			}
		}
	};
	private Family family;
	
	/**
	 * 
	 * @param family family that apply to this system (Box2DBodyModel is always included, no need to include it)
	 */
	public AbstractBox2DSystem(FamilyBuilder family) 
	{
		// update will be executed after box2D listener
		super(GamePipeline.AFTER_PHYSICS);
		// add box2D to family
		this.family = family.all(Box2DBodyModel.class).get();
	}
	
	abstract protected void registerListener(Entity entity, Box2DBodyModel physics);
	
	protected void unregisterListener(Entity entity, Box2DBodyModel physics){
		// TODO ?
	}

	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		engine.addEntityListener(family, listener);
	}
	
	@Override
	public void removedFromEngine(Engine engine) 
	{
		engine.removeEntityListener(listener);
		super.removedFromEngine(engine);
	}
}
