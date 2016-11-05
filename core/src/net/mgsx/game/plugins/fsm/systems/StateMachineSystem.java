package net.mgsx.game.plugins.fsm.systems;

import com.badlogic.ashley.core.Entity;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.systems.EventComponentIteratingSystem;
import net.mgsx.game.plugins.fsm.StateMachinePlugin;
import net.mgsx.game.plugins.fsm.components.StateMachineComponent;

public class StateMachineSystem extends EventComponentIteratingSystem<StateMachineComponent>
{
	
	public StateMachineSystem() {
		super(StateMachineComponent.class, GamePipeline.LOGIC);
	}
	@Override
	protected void processEntity(Entity entity, StateMachineComponent component, float deltaTime) {
		component.fsm.update();
	}
	@Override
	protected void entityAdded(Entity entity, StateMachineComponent component) 
	{
		component.fsm = StateMachinePlugin.fsmPool.obtain();
		component.fsm.setGlobalState(component.globalState);
		component.fsm.setOwner(entity);
		component.fsm.changeState(component.initialState);
	}
	@Override
	protected void entityRemoved(Entity entity, StateMachineComponent component) {
	}

}
