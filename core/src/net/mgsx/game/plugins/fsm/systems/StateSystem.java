package net.mgsx.game.plugins.fsm.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.systems.EventIteratingSystem;
import net.mgsx.game.plugins.fsm.components.StateComponent;

public class StateSystem extends EventIteratingSystem{

	private Class<? extends StateComponent> state;
	public StateSystem(Class<? extends StateComponent> state, Family family) {
		super(Family.all(state).get(), family, GamePipeline.LOGIC);
		this.state = state;
	}
	@Override
	public void entityAdded(Entity entity) {
		enter(entity);
	}

	@Override
	public void entityRemoved(Entity entity) {
		exit(entity);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		update(entity, deltaTime);
	}
	
	protected void change(Entity entity, Class<? extends StateComponent> newState)
	{
		entity.remove(state);
		entity.add(getEngine().createComponent(newState));
	}
	
	protected void enter(Entity entity) {
	}
	protected void update(Entity entity, float deltaTime) {
	}
	protected void exit(Entity entity) {
	}


}
