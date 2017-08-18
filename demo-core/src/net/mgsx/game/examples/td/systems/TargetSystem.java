package net.mgsx.game.examples.td.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.MultiTarget;
import net.mgsx.game.examples.td.components.Priority;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class TargetSystem extends IteratingSystem
{
	private ImmutableArray<Entity> enemies;
	private ImmutableArray<Entity> allies;
	
	private Array<Entity> candidates = new Array<Entity>();

	private static Comparator<Entity> enemyHomeNearest = new Comparator<Entity>() {
		@Override
		public int compare(Entity o1, Entity o2) {
			Enemy e1 = Enemy.components.get(o1);
			Enemy e2 = Enemy.components.get(o2);
			return Float.compare(e1.home, e2.home);
		}
	};
	private static Comparator<Entity> lifeMin = new Comparator<Entity>() {
		@Override
		public int compare(Entity o1, Entity o2) {
			Life e1 = Life.components.get(o1);
			Life e2 = Life.components.get(o2);
			float life1 = e1 != null ? e1.current : Float.MAX_VALUE;
			float life2 = e2 != null ? e2.current : Float.MAX_VALUE;
			return Float.compare(life1, life2);
		}
	};
	private static Comparator<Entity> lifeMax = new Comparator<Entity>() {
		@Override
		public int compare(Entity o1, Entity o2) {
			Life e1 = Life.components.get(o1);
			Life e2 = Life.components.get(o2);
			float life1 = e1 != null ? e1.current : Float.MAX_VALUE;
			float life2 = e2 != null ? e2.current : Float.MAX_VALUE;
			return Float.compare(life2, life1);
		}
	};
	
	public TargetSystem() {
		super(Family.all(Transform2DComponent.class).one(SingleTarget.class, MultiTarget.class).get(), GamePipeline.LOGIC);
	}
	
	public Entity findClosestOpponent(Entity entity) {
		final Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(transform == null) return null;
		boolean isEnemy = Enemy.components.has(entity);
		ImmutableArray<Entity> targets = isEnemy ? allies : enemies;
		candidates.clear();
		for(Entity enemyEntity : targets) candidates.add(enemyEntity);
		candidates.sort(new Comparator<Entity>() {
						@Override
						public int compare(Entity o1, Entity o2) {
							Transform2DComponent e1 = Transform2DComponent.components.get(o1);
							Transform2DComponent e2 = Transform2DComponent.components.get(o2);
							return Float.compare(e1.position.dst2(transform.position), e2.position.dst2(transform.position));
						}
					});
		
		if(candidates.size > 0) return candidates.first();
		
		return null;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		// choose target group, algorithm will call allies "enemies" when current
		// entity is enemy.
		boolean isEnemy = Enemy.components.has(entity);
		ImmutableArray<Entity> targets = isEnemy ? allies : enemies;
		
		final Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Range range = Range.components.get(entity);
		Priority priority = Priority.components.get(entity);
		MultiTarget multiTarget = MultiTarget.components.get(entity);
		SingleTarget singleTarget = SingleTarget.components.get(entity);
		
		// first check if entity can have more targets
		if(singleTarget != null)
		{
			if(singleTarget.target != null) return;
		}
		else if(multiTarget != null)
		{
			if(multiTarget.targets.size >= multiTarget.max) return;
		}
		
		if(targets.size() > 0){
			
			candidates.clear();
			
			// filter targets in range
			for(Entity enemyEntity : targets){
				if(range != null){
					Transform2DComponent targetTransform = Transform2DComponent.components.get(enemyEntity);
					if(targetTransform.position.dst2(transform.position) <= range.distance * range.distance){
						candidates.add(enemyEntity);
					}
				}else{
					candidates.add(enemyEntity);
				}
			}
			
			// sort by priority
			Comparator<Entity> comparator = null;
			
			if(priority != null && priority.current != null){
				// TODO could be optimized by storing comparator on target component ?
				switch (priority.current) {
				case CLOSE:
					comparator = new Comparator<Entity>() {
						@Override
						public int compare(Entity o1, Entity o2) {
							Transform2DComponent e1 = Transform2DComponent.components.get(o1);
							Transform2DComponent e2 = Transform2DComponent.components.get(o2);
							return Float.compare(e1.position.dst2(transform.position), e2.position.dst2(transform.position));
						}
					};
					break;
				case FAR:
					comparator = new Comparator<Entity>() {
						@Override
						public int compare(Entity o1, Entity o2) {
							Transform2DComponent e1 = Transform2DComponent.components.get(o1);
							Transform2DComponent e2 = Transform2DComponent.components.get(o2);
							return Float.compare(e2.position.dst2(transform.position), e1.position.dst2(transform.position));
						}
					};
					break;
				case DANGEROUS:
					comparator = isEnemy ? null : enemyHomeNearest;
					break;
				case RANDOM:
					break;
				case STRONG:
					comparator = lifeMax;
					break;
				case WEAK:
					comparator = lifeMin;
					break;
				default:
					break;
				}
			}
			
			if(comparator != null) candidates.sort(comparator);
			
			// make targets
			if(candidates.size > 0){
				
				if(singleTarget != null)
				{
					singleTarget.target = candidates.first();
				}
				else if(multiTarget != null)
				{
					for(Entity candidate : candidates){
						if(multiTarget.targets.size < multiTarget.max){
							multiTarget.targets.add(candidate);
						}else{
							break;
						}
					}
				}
			}
			
		}
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		allies = engine.getEntitiesFor(Family.all(Transform2DComponent.class, Life.class).exclude(Enemy.class).get());
		enemies = engine.getEntitiesFor(Family.all(Transform2DComponent.class, Enemy.class).get());

		
		// OPTIM could be optimized by storing link when singleTarget added (Map of entity Array)
		final ImmutableArray<Entity> singleTargetingEntities = engine.getEntitiesFor(Family.all(SingleTarget.class).get());
		engine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				for(Entity targetingEntity : singleTargetingEntities)
				{
					SingleTarget targetting = SingleTarget.components.get(targetingEntity);
					if(targetting.target == entity){
						// just remove reference since single target still aspect of entity but have to target another one.
						targetting.target = null;
					}
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
		
		final ImmutableArray<Entity> multiTargetingEntities = engine.getEntitiesFor(Family.all(MultiTarget.class).get());
		engine.addEntityListener(new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				for(Entity targetingEntity : multiTargetingEntities)
				{
					MultiTarget targetting = MultiTarget.components.get(targetingEntity);
					targetting.targets.removeValue(entity, true);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
	}

	
}
