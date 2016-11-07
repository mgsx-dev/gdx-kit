package net.mgsx.game.examples.platformer.core.states;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.fsm.components.StateComponent;
import net.mgsx.game.plugins.fsm.systems.EntityStateSystem;
import net.mgsx.game.plugins.g3d.G3DModel;

public class FlyingState extends EntityStateSystem
{
	public static class FlyingComponent implements StateComponent
	{
		// put flying relative data here
	}
	
	public FlyingState() {
		super(FlyingComponent.class, configure().all(G3DModel.class, Box2DBodyModel.class));
	}

	@Override
	protected void enter(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update(Entity entity, float deltaTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void exit(Entity entity) {
		// TODO Auto-generated method stub
		
	}
	

	


}
