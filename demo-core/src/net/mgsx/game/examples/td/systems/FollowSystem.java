package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.td.components.Follow;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.core.systems.DependencySystem;
import net.mgsx.game.plugins.core.systems.DependencySystem.LinkListener;

public class FollowSystem extends IteratingSystem
{
	private static LinkListener unlinkListener = new LinkListener() {
		@Override
		public void onUnlink(Entity source, Entity target) {
			source.remove(Follow.class);
		}
	};
	
	@Inject
	public DependencySystem dependencySystem;
	
	private Vector2 delta = new Vector2();
	public FollowSystem() {
		super(Family.all(Follow.class, Speed.class, Transform2DComponent.class).get(), GamePipeline.AFTER_LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		engine.addEntityListener(Family.all(Follow.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
			}
			
			@Override
			public void entityAdded(Entity entity) {
				Follow follow = Follow.components.get(entity);
				dependencySystem.link(entity, follow.head, unlinkListener);
			}
		});
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Follow follow = Follow.components.get(entity);
		Speed speed = Speed.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(follow == null || follow.head == null) {
			return; 
			// XXX ??
		}
		Transform2DComponent targetTransform = Transform2DComponent.components.get(follow.head);
		if(targetTransform == null) return;
		
		delta.set(targetTransform.position).sub(transform.position);
		float distance = delta.len();
		delta.nor();
		float angle = delta.angle();
		
		// transform.angle
		if(distance > follow.maxDistance)
			transform.position.mulAdd(delta, speed.current * deltaTime);
		else if(distance < follow.minDistance)
			transform.position.mulAdd(delta, -speed.current * deltaTime);
	}

}
