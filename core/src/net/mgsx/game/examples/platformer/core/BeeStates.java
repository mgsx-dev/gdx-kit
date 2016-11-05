package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.plugins.box2d.model.Box2DBodyModel;
import net.mgsx.game.plugins.fsm.components.StateComponent;
import net.mgsx.game.plugins.fsm.systems.StateSystem;
import net.mgsx.game.plugins.g3d.G3DModel;

public class BeeStates 
{
	public static class InitState implements StateComponent{}
	public static class FlyState implements StateComponent{}
	public static class EatingState implements StateComponent{}
	
	private static class FlySystem extends StateSystem
	{
		public FlySystem() 
		{
			super(FlyState.class, Family.all(FlyState.class, G3DModel.class, Box2DBodyModel.class).get());
		}
		
		@Override
		protected void update(Entity entity, float deltaTime) {
			deltaTime = 0;
		}
	}
	
	public static void create(Engine engine)
	{
		engine.addSystem(new StateSystem(InitState.class,Family.all(InitState.class, G3DModel.class, Box2DBodyModel.class).get())
		{
			@Override
			protected void enter(Entity entity) 
			{
				change(entity, FlyState.class);
			}
		});
		
		engine.addSystem(new FlySystem());
		
		engine.addSystem(new StateSystem(EatingState.class,Family.all(EatingState.class, G3DModel.class, Box2DBodyModel.class).get())
		{
			@Override
			protected void update(Entity entity, float deltaTime) {
			}
		});
	}
}
