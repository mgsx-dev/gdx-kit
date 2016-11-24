package net.mgsx.game.plugins.fsm.components;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * Base state for all entities.
 * 
 * @author mgsx
 *
 */
abstract public class EntityState implements State<Entity>
{
	protected final Family family;

	public EntityState(Family family) {
		super();
		this.family = family;
	}
	
	public Family getFamily() {
		return family;
	}
	
	private StateMachineComponent fsm(Entity entity){
		return StateMachineComponent.components.get(entity);
	}
	
	abstract protected void enter(Entity entity, EntityStateMachine fsm);
	abstract protected void exit(Entity entity, EntityStateMachine fsm);
	abstract protected void update(Entity entity, EntityStateMachine fsm);

	@Override
	public void enter(Entity entity) {
		if(check(entity)){
			enter(entity, fsm(entity).fsm);
		}
	}
	
	private boolean check(Entity entity){
		if(!family.matches(entity)){
			if(fsm(entity).initialState != null && fsm(entity).initialState.family.matches(entity)){
				changeState(entity, fsm(entity).initialState);
			}
			else{
				changeState(entity, null);
			}
			return false;
		}
		return true;
	}
	
	@Override
	public void update(Entity entity) 
	{
		if(check(entity)){
			update(entity, fsm(entity).fsm);
		}
	}
	
	@Override
	public void exit(Entity entity) {
		if(family.matches(entity)){
			exit(entity, fsm(entity).fsm);
		}
	}
	
	public void changeState(Entity entity, EntityState newState)
	{
		StateMachineComponent fsm = StateMachineComponent.components.get(entity);
		fsm.fsm.changeState(newState);
	}
	
	@Override
	public boolean onMessage(Entity entity, Telegram telegram) {
		return false;
	}
	
}
