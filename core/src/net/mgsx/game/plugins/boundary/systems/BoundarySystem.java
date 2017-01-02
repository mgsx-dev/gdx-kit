package net.mgsx.game.plugins.boundary.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;

public class BoundarySystem extends IteratingSystem
{
	private GameScreen game;
	
	public BoundarySystem(GameScreen game) 
	{
		super(Family.one(BoundaryComponent.class).get(), GamePipeline.BEFORE_LOGIC);
		this.game = game;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		boundary.justInside = boundary.justOutside = false;
		boolean inside = false;
		
		inside = inside || game.camera.frustum.boundsInFrustum(boundary.box);
		
		if(inside && !boundary.inside){
			boundary.justInside = true;
		}
		else if(!inside && boundary.inside){
			boundary.justOutside = true;
		}
		boundary.inside = inside;
	}

}
