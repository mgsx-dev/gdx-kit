package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.Road;
import net.mgsx.game.examples.td.components.TileComponent;

public class MapSystem extends EntitySystem
{
	public static final int [][] ADJ_MATRIX = {{-1,0}, {0,1}, {1,0}, {0,-1}};
	
	private Entity [] tiles;
	public int width = 32, height = 32;
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
				for(int y=0 ; y<height ; y++){
					for(int x=0 ; x<width ; x++){
						if(entity == tiles[y * width + x]){
							tiles[y * width + x] = null;
							break;
						}
					}
				}
				invalidate();
			}
			
			@Override
			public void entityAdded(Entity entity) {
				TileComponent tile = TileComponent.components.get(entity);
				tiles[tile.y * width + tile.x] = entity;
				invalidate();
			}
		});
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
					Road road = Road.components.get(cell);
					if(road != null){
						road.home = -1;
					}
				}
			}
			
			Array<Entity> heads = new Array<Entity>();
			for(Entity e : getEngine().getEntitiesFor(Family.all(TileComponent.class, Home.class, Road.class).get())){
				heads.add(e);
			}
			
			for(Entity head : heads){
				Road road = Road.components.get(head);
				road.home = 0;
			}
			
			Array<Entity> nextHeads = new Array<Entity>();
			while(heads.size > 0){
				for(Entity head : heads){
					TileComponent tile = TileComponent.components.get(head);
					Road road = Road.components.get(head);
					if(road != null){
						for(int [] v : ADJ_MATRIX){
							Entity adj = getTile(tile.x + v[0], tile.y + v[1]);
							if(adj != null){
								Road adjRoad = Road.components.get(adj);
								if(adjRoad != null && (adjRoad.home == -1 || adjRoad.home > road.home + 1)){
									adjRoad.home = road.home + 1;
									nextHeads.add(adj);
								}
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

	public void invalidate() {
		valid = false;
	}
}
