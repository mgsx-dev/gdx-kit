package net.mgsx.game.examples.platformer.states;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.examples.platformer.logic.PlatformerAI;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.fsm.StateMachinePlugin;
import net.mgsx.game.plugins.fsm.components.EntityState;
import net.mgsx.game.plugins.fsm.components.EntityStateMachine;

public class GuardStateMachine 
{
	private static final Family guardFamily = Family.all(Box2DBodyModel.class, PlatformerAI.class).get();
	
	public static class WALKING extends EntityState {
		
		private Vector2 veclocity = new Vector2();
		
		public WALKING() {
			super(guardFamily);
		}
		
		@Override
		protected void enter(Entity entity, EntityStateMachine fsm) {
			PlatformerAI ai = PlatformerAI.components.get(entity);
			Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
			ai.direction.set(-1, 0);
			ai.walkTime = 0;
			ai.initialPosition.set(physics.body.getPosition());
		}
		
		@Override
		protected void update(Entity entity, EntityStateMachine fsm) {
			PlatformerAI ai = PlatformerAI.components.get(entity);
			Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
			
			Vector2 diff = ai.initialPosition.cpy().sub(physics.body.getPosition());
			float dist = diff.len();
			
			if(dist > 4.f){
				ai.direction.x = diff.x < 0 ? -1 : 1;
			}else{
				if(physics.context.provider.rayCastFirst(physics.body.getPosition(), ai.direction, 1.f) != null){
					if(diff.x  * ai.direction.x > 0 ){
						fsm.changeState(StateMachinePlugin.state(WAITING.class));
					}
					else ai.direction.x = diff.x < 0 ? -1 : 1;
				}
			}
			physics.body.setLinearVelocity(veclocity.set(ai.direction).scl(1.3f));
		}
		
		@Override
		protected void exit(Entity entity, EntityStateMachine fsm) {
			
		}
		
	};
	
	public static class WAITING extends EntityState {
		
		public WAITING() {
			super(guardFamily);
		}

		@Override
		protected void enter(Entity entity, EntityStateMachine fsm) {
			Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
			physics.body.setLinearVelocity(0, 0);
		}

		@Override
		protected void exit(Entity entity, EntityStateMachine fsm) {
			
		}

		@Override
		protected void update(Entity entity, EntityStateMachine fsm) {
			
		}
	}
	
}
