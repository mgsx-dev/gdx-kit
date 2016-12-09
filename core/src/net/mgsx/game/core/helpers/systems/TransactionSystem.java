package net.mgsx.game.core.helpers.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

abstract public class TransactionSystem extends EntitySystem
{
	private EntitySystem afterSystem;
	public TransactionSystem(int beforePriority, int afterPriority) {
		super(beforePriority);
		afterSystem = new EntitySystem(afterPriority) {
			@Override
			public void update(float deltaTime) {
				updateAfter(deltaTime);
			}
		};
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addSystem(afterSystem);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeSystem(afterSystem);
		super.removedFromEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		updateBefore(deltaTime);
	}
	
	@Override
	public void setProcessing(boolean processing) {
		super.setProcessing(processing);
		afterSystem.setProcessing(processing);
	}
	
	abstract protected void updateBefore(float deltaTime);
	abstract protected void updateAfter(float deltaTime);
	
}
