package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.core.components.Repository;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.Road;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class MapSystem extends EntitySystem
{
	public static final int [][] ADJ_MATRIX = {{-1,0}, {0,1}, {1,0}, {0,-1}};
	
	@Asset("td/monster.g3dj")
	public Model monsterModel;
	
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
				
//				Transform2DComponent transform = getEngine().createComponent(Transform2DComponent.class);
//				transform.position.set(tile.x + .5f, tile.y + .5f);
//				entity.add(transform);
			}
		});
	}
	
	public Entity getTile(int x, int y) {
		if(x >= 0 && x < width && y >= 0 && y < height){
			return tiles[y * width + x];
		}
		return null;
	}
	
	/**
	 * retrieve a tile, create it if not exist yet and in map bounds
	 * @param x
	 * @param y
	 * @return the tile or null if not in bounds
	 */
	public Entity getOrCreateTile(int x, int y) 
	{
		if(x >= 0 && x < width && y >= 0 && y < height){
			Entity cell = tiles[y * width + x];
			if(cell == null)
			{
				cell = getEngine().createEntity();
				TileComponent tile = getEngine().createComponent(TileComponent.class);
				tile.x = x;
				tile.y = y;
				cell.add(tile);
				cell.add(getEngine().createComponent(Repository.class));
				getEngine().addEntity(cell);
			}
			return cell;
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

	public Array<Entity> getEntities(Array<Entity> entities, Family family, Rectangle bounds) 
	{
		// OPTIM could be optimized with quadtree
		for(Entity entity : getEngine().getEntitiesFor(family))
		{
			Transform2DComponent transform = Transform2DComponent.components.get(entity);
			if(bounds.contains(transform.position))
			{
				entities.add(entity);
			}
		}
		return entities;
	}

	private Array<Entity> path = new Array<Entity>();
	private Array<Entity> candidates = new Array<Entity>();
	
	public Entity findDirectPathToHome(PathFollower pathFollower, int x, int y) 
	{
		// TODO might be no tile but transform
		Entity homeEntity = getEngine().getEntitiesFor(Family.all(Home.class, TileComponent.class).get()).first();
		TileComponent homeTile = TileComponent.components.get(homeEntity);
		Vector2 target = new Vector2(homeTile.x + .5f, homeTile.y + .5f);
		
		Vector2 source = new Vector2(x + .5f, y + .5f);
		
		Bezier<Vector2> curve = new Bezier<Vector2>(source, target);
		
		// create spline
		pathFollower.path = curve;
		pathFollower.length = source.dst(target);
		
		return homeEntity;

	}
	public Entity findPathToHome(PathFollower pathFollower, int x, int y) 
	{
		Entity homeEntity = null;
		
		// find logical path and construct some random points for a catmullrom spline
		path.clear();
		Entity head = getTile(x, y);
		while(head != null)
		{
			Road road = Road.components.get(head);
			TileComponent tile = TileComponent.components.get(head);
			
			path.add(head);
			
			// find candidates neighboor
			candidates.clear();
			for(int [] v : ADJ_MATRIX){
				Entity adj = getTile(tile.x + v[0], tile.y + v[1]);
				if(adj != null){
					Road adjRoad = Road.components.get(adj);
					if(adjRoad != null && adjRoad.home < road.home){
						candidates.add(adj);
					}
				}
			}
			if(candidates.size > 1){
				head = candidates.get(MathUtils.random(candidates.size-1));
			}else if(candidates.size > 0){
				head = candidates.first();
			}else{
				head = null;
			}
		}
		
		// create points
		float d = .1f; // dispersion
		Vector2 [] points = new Vector2[path.size + 2];
		for(int i=0 ; i<path.size ; i++)
		{
			TileComponent tile = TileComponent.components.get(path.get(i));
			points[i+1] = new Vector2(tile.x + .5f + MathUtils.random(-d, d), tile.y + .5f + MathUtils.random(-d, d));
		}
		points[0] = new Vector2(points[1]);
		points[points.length-1] = new Vector2(points[points.length-2]);
		
		// create spline
		pathFollower.path = new CatmullRomSpline<Vector2>(points, false);
		pathFollower.length = pathFollower.path.approxLength(path.size * 10); // 10 sub segment per segment TODO maybe use a fixed sample count for stability ?
	
		homeEntity = path.peek();
		if(!Home.components.has(homeEntity)) homeEntity = null; // XXX not sure ...
		
		return homeEntity;
	}
}
