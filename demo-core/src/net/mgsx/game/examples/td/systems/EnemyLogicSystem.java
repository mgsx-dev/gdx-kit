package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class EnemyLogicSystem extends IteratingSystem
{
	private Array<Entity> adjs = new Array<Entity>();
	public EnemyLogicSystem() {
		super(Family.all(Enemy.class, Transform2DComponent.class, PathFollower.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		PathFollower path = PathFollower.components.get(entity);
		
		Enemy enemy = Enemy.components.get(entity);
		Life life = Life.components.get(entity);
		if(life.current <= 0){
			getEngine().removeEntity(entity);
			return;
		}
		
		MapSystem map = getEngine().getSystem(MapSystem.class);
		path.t += deltaTime * path.speed;
		if(path.t > 1){
			path.t -= 1;
			path.sx = path.tx;
			path.sy = path.ty;
			
			
			Entity cell = map.getTile(path.sx, path.sy);
			if(cell == null){
				// XXX case of just removed cell
				getEngine().removeEntity(entity);
				return;
			}
			
			Home home = Home.components.get(cell);
			if(home != null){
				Life homeLife = Life.components.get(cell);
				if(homeLife != null){
					homeLife.current -= 1; // XXX hard coded !
				}
				getEngine().removeEntity(entity);
				return;
			}
			// TODO find next cell
			adjs.clear();
			TileComponent currentTile = TileComponent.components.get(cell);
			for(int [] v : MapSystem.ADJ_MATRIX){
				Entity adj = map.getTile(path.sx + v[0], path.sy + v[1]);
				if(adj != null){
					TileComponent tile = TileComponent.components.get(adj);
					if(tile.home < currentTile.home){
						adjs.add(adj);
					}
				}
			}
			if(adjs.size > 0){
				Entity target = adjs.get(MathUtils.random(adjs.size-1));
				TileComponent targetTile = TileComponent.components.get(target);
				path.tx = targetTile.x;
				path.ty = targetTile.y;
			}else{
				// XXX no path to home ...
				getEngine().removeEntity(entity);
				return;
			}
			
		}
		
		// TODO could not exist anymore ... check for null entity before !
		TileComponent srcTile = TileComponent.components.get(map.getTile(path.sx, path.sy));
		TileComponent dstTile = TileComponent.components.get(map.getTile(path.tx, path.ty));
		
		enemy.home = MathUtils.lerp(srcTile.home, dstTile.home, path.t);
		
		transform.position.set(MathUtils.lerp(path.sx, path.tx, path.t) + .5f, MathUtils.lerp(path.sy, path.ty, path.t) + .5f);
	}
}
