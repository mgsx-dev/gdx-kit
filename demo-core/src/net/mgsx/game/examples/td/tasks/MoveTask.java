package net.mgsx.game.examples.td.tasks;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.examples.platformer.logic.LifeComponent;
import net.mgsx.game.examples.td.components.Direction;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Entry;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.examples.td.components.Tower;
import net.mgsx.game.plugins.btree.BTreePlugin.EntityLeafTask;
import net.mgsx.game.plugins.btree.annotations.TaskAlias;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@TaskAlias("moveTarget")
public class MoveTask extends EntityLeafTask {
	
	public static enum Priority{
		CLOSEST{
			@Override
			public Comparator<Entity> comparator(Entity entity) {
				final Transform2DComponent base = Transform2DComponent.components.get(entity);
				return new Comparator<Entity>() {
					@Override
					public int compare(Entity a, Entity b) {
						Transform2DComponent ta = Transform2DComponent.components.get(a);
						Transform2DComponent tb = Transform2DComponent.components.get(b);
						return Float.compare(base.position.dst2(ta.position), base.position.dst2(tb.position));
					}
				};
			}
		}, 
		FAREST{
			@Override
			public Comparator<Entity> comparator(Entity entity) {
				final Transform2DComponent base = Transform2DComponent.components.get(entity);
				return new Comparator<Entity>() {
					@Override
					public int compare(Entity a, Entity b) {
						Transform2DComponent ta = Transform2DComponent.components.get(a);
						Transform2DComponent tb = Transform2DComponent.components.get(b);
						return -Float.compare(base.position.dst2(ta.position), base.position.dst2(tb.position));
					}
				};
			}
		}, 
		WEAKEST{
			@Override
			public Comparator<Entity> comparator(Entity entity) {
				return new Comparator<Entity>() {
					@Override
					public int compare(Entity a, Entity b) {
						LifeComponent ta = LifeComponent.components.get(a);
						LifeComponent tb = LifeComponent.components.get(b);
						return Float.compare(ta.life, tb.life);
					}
				};
			}
		}, 
		STRONGEST{
			@Override
			public Comparator<Entity> comparator(Entity entity) {
				return new Comparator<Entity>() {
					@Override
					public int compare(Entity a, Entity b) {
						LifeComponent ta = LifeComponent.components.get(a);
						LifeComponent tb = LifeComponent.components.get(b);
						return -Float.compare(ta.life, tb.life);
					}
				};
			}
		};

		public Comparator<Entity> comparator(Entity entity) {
			return null;
		}
	}
	
	public static enum Target{
		ALLY, ENEMY, ALLY_BASE, ENEMY_BASE;
	}
	
	public static enum Mode{
		TO, FROM;
	}
	
	@TaskAttribute
	public Priority priority = Priority.CLOSEST;
	
	@TaskAttribute
	public Target target = Target.ENEMY;
	
	@TaskAttribute
	public Mode direction = Mode.TO;
	
	@TaskAttribute
	public float rangeRatio = .5f;
	
	private Vector2 targetPosition;
	
	@Override
	public void reset() {
		super.reset();
		targetPosition = null;
	}
	
	@Override
	public com.badlogic.gdx.ai.btree.Task.Status execute() 
	{
		if(targetPosition == null)
		{
			boolean isEnemy = Enemy.components.has(getEntity());
			Entity target;
			ImmutableArray<Entity> entities;
			if(isEnemy)
			{
				switch (this.target) {
				default:
				case ALLY:
					entities = getEngine().getEntitiesFor(Family.all(Enemy.class).get());
					break;
				case ALLY_BASE:
					entities = getEngine().getEntitiesFor(Family.all(Entry.class).get());
					break;
				case ENEMY:
					entities = getEngine().getEntitiesFor(Family.all(Tower.class).get());
					break;
				case ENEMY_BASE:
					entities = getEngine().getEntitiesFor(Family.all(Home.class).get());
					break;
				}
			}
			else
			{
				switch (this.target) {
				default:
				case ALLY:
					entities = getEngine().getEntitiesFor(Family.all(Tower.class).get());
					break;
				case ALLY_BASE:
					entities = getEngine().getEntitiesFor(Family.all(Home.class).get());
					break;
				case ENEMY:
					entities = getEngine().getEntitiesFor(Family.all(Enemy.class).get());
					break;
				case ENEMY_BASE:
					entities = getEngine().getEntitiesFor(Family.all(Entry.class).get());
					break;
				}
				
			}
			
			// go to closest entry
			if(entities.size() == 0) return Status.FAILED;
			
			Array<Entity> candidates = ArrayHelper.array(entities);
			candidates.removeValue(getEntity(), true);
			candidates.sort(priority.comparator(getEntity()));
			target = candidates.first();
			
			Transform2DComponent targetTransform = Transform2DComponent.components.get(target);
			if(targetTransform != null)
			{
				targetPosition = new Vector2(targetTransform.position);
			}
			else
			{
				TileComponent tile = TileComponent.components.get(target);
				if(tile == null) return Status.FAILED;
				targetPosition = new Vector2(tile.x + .5f, tile.y + .5f);
			}
		}
		
		Transform2DComponent transform = Transform2DComponent.components.get(getEntity());
		
		float d = .1f; // TODO min
		Range range = Range.components.get(getEntity());
		if(range != null) d = range.distance * rangeRatio;
		
		Speed speed = Speed.components.get(getEntity());
		
		if(direction == Mode.TO)
		{
			if(transform.position.dst2(targetPosition) < d * d)
			{
				getEntity().remove(Direction.class);
				targetPosition = null;
				speed.base = 0;
				return Status.SUCCEEDED;
			}
			
			Direction direction = Direction.components.get(getEntity());
			if(direction == null){
				direction = getEngine().createComponent(Direction.class);
				getEntity().add(direction);
			}
			
			direction.vector.set(targetPosition).sub(transform.position).nor();
			transform.angle = direction.vector.angle();
		}
		else
		{
			if(transform.position.dst2(targetPosition) > d * d)
			{
				getEntity().remove(Direction.class);
				targetPosition = null;
				speed.base = 0;
				return Status.SUCCEEDED;
			}
			
			Direction direction = Direction.components.get(getEntity());
			if(direction == null){
				direction = getEngine().createComponent(Direction.class);
				getEntity().add(direction);
			}
			
			direction.vector.set(transform.position).sub(targetPosition).nor();
			transform.angle = direction.vector.angle();
		}
		speed.base = 1;
		
		
		return Status.RUNNING;
		
	}		
}
