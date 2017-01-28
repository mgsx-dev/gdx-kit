package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Shot;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.examples.td.components.Tower;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class TowerLogicSystem extends IteratingSystem
{
	private ImmutableArray<Entity> enemies;
	public TowerLogicSystem() {
		super(Family.all(Tower.class, TileComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		enemies = engine.getEntitiesFor(Family.all(Transform2DComponent.class, Enemy.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Tower tower = Tower.components.get(entity);
		tower.reload -= deltaTime;
		if(tower.reload < 0){
			
			// choose a target
			if(enemies.size() > 0){
				tower.reload += tower.reloadRequired;
				
				TileComponent tile = TileComponent.components.get(entity);
				
				Entity targetEntity = enemies.get(MathUtils.random(enemies.size()-1));
				Transform2DComponent target = Transform2DComponent.components.get(targetEntity);
				Enemy enemy = Enemy.components.get(targetEntity);
				enemy.life -= tower.reloadRequired; // XXX before impact .... // XXX depends on reload ...
				
				Entity shotEntity = getEngine().createEntity();
				Shot shot = getEngine().createComponent(Shot.class);
				shot.start.set(tile.x+.5f, tile.y+.5f);
				shot.end.set(target.position);
				shotEntity.add(shot);
				getEngine().addEntity(shotEntity);
				
			}else{
				tower.reload = 0;
			}
			
		}
	}
}
