package net.mgsx.game.examples.platformer.ai;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.animations.Character2D;
import net.mgsx.game.examples.platformer.states.WalkingState;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;

public class PatrolSystem extends IteratingSystem
{
	private Vector2 direction = new Vector2();
	
	public PatrolSystem() {
		super(Family.all(PatrolComponent.class, PatrolState.class, Box2DBodyModel.class, Character2D.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(getFamily(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				entity.remove(WalkingState.class);
				Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
				if(physics != null){
					physics.body.setLinearVelocity(0, 0);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				entity.add(getEngine().createComponent(WalkingState.class));
			}
		});
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		PatrolComponent patrol = PatrolComponent.components.get(entity);
		Character2D character = Character2D.components.get(entity);
		Vector2 vel = physics.body.getLinearVelocity();
		
		// check walls
		if(patrol.timeout > 0){
			vel.x = 0;
			patrol.timeout-=deltaTime;
		}else{
			
			for(int i=0 ; i<2 ; i++)
			{
				boolean shouldChangeDirection = false;
				
				float xDir = character.rightToLeft ? -1 : 1;
				
				// check void
				if(patrol.checkVoid){
					if(physics.context.rayCastFirst(physics.body.getPosition().add(patrol.rayStart), direction.set(xDir, -1), patrol.horizon) == null)
					{
						shouldChangeDirection = true;
					}
				}
				// check walls
				if(!shouldChangeDirection){
					if(physics.context.rayCastFirst(physics.body.getPosition().add(patrol.rayStart), direction.set(xDir, 0), patrol.horizon) != null)
					{
						shouldChangeDirection = true;
					}
				}
				
				if(shouldChangeDirection)
				{
					if(i == 0){
						character.rightToLeft = !character.rightToLeft;
					}
					else
					{
						patrol.timeout = 1f;
					}
				}
				vel.x = patrol.speed * xDir;
			}
		}
		physics.body.setLinearVelocity(vel);
	}
}
