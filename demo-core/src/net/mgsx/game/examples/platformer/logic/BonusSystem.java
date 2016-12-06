package net.mgsx.game.examples.platformer.logic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class BonusSystem extends IteratingSystem
{
	private final EntityListener listener = new EntityListener() {
		
		@Override
		public void entityRemoved(Entity entity) {
		}
		
		@Override
		public void entityAdded(Entity entity) {
			// TODO use mappers
			entity.getComponent(G3DModel.class).animationController.paused = false;
			entity.getComponent(G3DModel.class).animationController.setAnimation("apple.lp|apple.lpAction", -1);
			
			// TODO remove
			if(entity.getComponent(Transform2DComponent.class) == null) entity.add(new Transform2DComponent());
		}
	};
	
	public BonusSystem() {
		super(Family.all(G3DModel.class, Box2DBodyModel.class, BonusComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(getFamily(), listener);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		engine.removeEntityListener(listener);
		super.removedFromEngine(engine);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// nothing special for now : soon a state machine to update ...
	}

}
