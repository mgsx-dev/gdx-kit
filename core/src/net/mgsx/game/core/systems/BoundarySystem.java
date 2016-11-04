package net.mgsx.game.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.BoundaryComponent;

public class BoundarySystem extends IteratingSystem
{
	private GameEngine engine;
	
	public BoundarySystem(GameEngine engine) 
	{
		super(Family.one(BoundaryComponent.class).get(), GamePipeline.BEFORE_LOGIC);
		this.engine = engine;
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		boundary.justInside = boundary.justOutside = false;
		boolean inside = engine.gameCamera.frustum.boundsInFrustum(boundary.box);
		if(inside && !boundary.inside){
			boundary.justInside = true;
		}
		else if(!inside && boundary.inside){
			boundary.justOutside = true;
		}
		boundary.inside = inside;
	}

}
