package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.Telegram;

import net.mgsx.game.plugins.fsm.components.EntityState;
import net.mgsx.game.plugins.fsm.components.StateMachineComponent;

public enum BeeState implements EntityState
{
	INIT(){
		@Override
		public void enter(Entity entity) {
			BeeComponent bee = BeeComponent.components.get(entity);
			if(bee != null){
				bee.life = 0;
			}
			// TODO register some entities like model 3D, animations, ...etc ?
			StateMachineComponent fsm = StateMachineComponent.components.get(entity);
			fsm.fsm.changeState(FLY);
		}
	},
	
	FLY(){
		@Override
		public void update(Entity entity) 
		{
			// TODO get model and move it in sin way
			super.update(entity);
		}
	}
	
	;

	@Override
	public void enter(Entity entity) {
	}

	@Override
	public void update(Entity entity) {
	}

	@Override
	public void exit(Entity entity) {
	}

	@Override
	public boolean onMessage(Entity entity, Telegram telegram) {
		return false;
	}

}
