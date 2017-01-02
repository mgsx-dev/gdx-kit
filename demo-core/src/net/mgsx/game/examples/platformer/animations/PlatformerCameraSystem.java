package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class PlatformerCameraSystem extends IteratingSystem
{
	private ImmutableArray<Entity> focusEntities;
	
	private Vector3 focus = new Vector3();
	
	public PlatformerCameraSystem() 
	{
		super(Family.all(CameraComponent.class, PlayerTracker.class).get(), GamePipeline.BEFORE_CULLING);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		focusEntities = engine.getEntitiesFor(Family.all(FocusComponent.class, Transform2DComponent.class).get());
	}

	@Override
	public void update(float deltaTime) {
		
		// compute focus point
		focus.setZero();
		for(Entity player : focusEntities){
			Transform2DComponent transform = Transform2DComponent.components.get(player);
			if(transform != null) focus.add(transform.position.x, transform.position.y, 0);
		}
		if(focusEntities.size() > 0) focus.scl(1.f / focusEntities.size());
		
		super.update(deltaTime);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		CameraComponent camera = CameraComponent.components.get(entity);
		PlayerTracker tracker = PlayerTracker.components.get(entity);
		
		camera.camera.position.lerp(focus.cpy().add(tracker.offset), tracker.smoothness);
	}
}
