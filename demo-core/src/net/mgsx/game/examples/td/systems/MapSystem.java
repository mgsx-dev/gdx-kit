package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.TileComponent;

public class MapSystem extends EntitySystem
{
	public static final int [][] ADJ_MATRIX = {{-1,0}, {0,1}, {1,0}, {0,-1}};
	
	private Entity [] tiles;
	private int width = 32, height = 32;
	private boolean valid = false;
	
	public MapSystem() {
		super(GamePipeline.LOGIC);
		tiles = new Entity[width * height];
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		engine.addEntityListener(Family.all(TileComponent.class).get(), new EntityListener() {
			
			@Override
			public void entityRemoved(Entity entity) {
				// TODO find which is removed in map
				valid = false;
			}
			
			@Override
			public void entityAdded(Entity entity) {
				TileComponent tile = TileComponent.components.get(entity);
				tiles[tile.y * width + tile.x] = entity;
				valid = false;
			}
		});
	}
	
	public Entity switchTile(int x, int y){
		if(x >= 0 && x < width && y >= 0 && y < height){
			Entity entity = tiles[y * width + x];
			if(entity == null){
				entity = getEngine().createEntity();
				TileComponent tile = getEngine().createComponent(TileComponent.class);
				tile.x = x;
				tile.y = y;
				entity.add(tile);
				tiles[y * width + x] = entity;
				getEngine().addEntity(entity);
				valid = false;
				return entity;
			}else if(entity != null){
				getEngine().removeEntity(entity);
				tiles[y * width + x] = null;
				valid = false;
			}
		}
		return null;
	}

	public Entity getTile(int x, int y) {
		if(x >= 0 && x < width && y >= 0 && y < height){
			return tiles[y * width + x];
		}
		return null;
	}
	
	@Override
	public void update(float deltaTime)
	{
		if(!valid){
			valid = true;
			
			// compute graph (breadth search)
			for(Entity cell : tiles){
				if(cell != null){
					TileComponent tile = TileComponent.components.get(cell);
					tile.home = -1;
				}
			}
			
			Array<Entity> heads = new Array<Entity>();
			for(Entity e : getEngine().getEntitiesFor(Family.all(TileComponent.class, Home.class).get())){
				heads.add(e);
			}
			
			for(Entity head : heads){
				TileComponent tile = TileComponent.components.get(head);
				tile.home = 0;
			}
			
			Array<Entity> nextHeads = new Array<Entity>();
			while(heads.size > 0){
				for(Entity head : heads){
					TileComponent tile = TileComponent.components.get(head);
					for(int [] v : ADJ_MATRIX){
						Entity adj = getTile(tile.x + v[0], tile.y + v[1]);
						if(adj != null){
							TileComponent adjTile = TileComponent.components.get(adj);
							if(adjTile.home == -1 || adjTile.home > tile.home + 1){
								adjTile.home = tile.home + 1;
								nextHeads.add(adj);
							}
						}
					}
				}
				heads.clear();
				heads.addAll(nextHeads);
				nextHeads.clear();
			}
			
		}
	}
}
