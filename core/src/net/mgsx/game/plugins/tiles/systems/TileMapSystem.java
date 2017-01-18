package net.mgsx.game.plugins.tiles.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.plugins.btree.BTreeModel;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.tiles.components.TileMapComponent;

public class TileMapSystem extends IteratingSystem
{
	private GameScreen game;
	public TileMapSystem(GameScreen game) {
		super(Family.all(TileMapComponent.class).get(), GamePipeline.BEFORE_CULLING);
		this.game = game;
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TileMapComponent map = TileMapComponent.components.get(entity);
		
		// initialize if not already initialized
		if(map.entityMap == null){
			// allocate necessary (from camera + extra + padding)
			map.defaultTile = map.map.getProperties().get("DefaultName", String.class);
			map.worldTileWidth = map.map.getProperties().get("WorldTileWidth", float.class);
			map.worldTileHeight = map.map.getProperties().get("WorldTileHeight", float.class);
			
			map.width = map.map.getProperties().get("width", int.class);
			map.height = map.map.getProperties().get("height", int.class);
			map.tileWidth = map.map.getProperties().get("tilewidth", int.class);
			map.tileHeight = map.map.getProperties().get("tileheight", int.class);
			
			map.entityMap = new EntityGroup[map.width * map.height];
			
			float ofx = 0, ofy = 0;
			
//			TiledMap tiledMap = (TiledMap)map.map;
//			for(TiledMapTileSet tileSet : tiledMap.getTileSets()){
//				for(TiledMapTile tile : tileSet){
//					Integer w = tile.getProperties().get("width", Integer.class);
//					Integer h = tile.getProperties().get("height", Integer.class);
//					if(w != null && h != null){
//						
//					}
//				}
//			}
			
			// find tiles and objects
			for(MapLayer layer : map.map.getLayers()){
				if(layer instanceof TiledMapTileLayer){
					
					TiledMapTileLayer tileLayer = (TiledMapTileLayer)layer;
					for(int y=0 ; y<map.height ; y++){
						for(int x=0 ; x<map.width ; x++){
							Cell cell = tileLayer.getCell(x, y);
							if(cell != null && cell.getTile() != null){
								Boolean dummy = cell.getTile().getProperties().get("dummy", Boolean.class);
								if(dummy != null && dummy) continue;
								
								String name = cell.getTile().getProperties().get("name", String.class);
								if(name == null){
									name = map.defaultTile;
								}
								String fileName = name + ".json";
								if(!Gdx.files.internal(fileName).exists()){
									Gdx.app.error("TileMap", "Missing linked file " + fileName);
									fileName = map.defaultTile + ".json";
								}
								LoadConfiguration config = new LoadConfiguration();
								config.assets = game.assets;
								config.engine = game.entityEngine;
								config.registry = game.registry;
								config.failSafe = true;
								EntityGroup group = new EntityGroup();
								
								group.entities().addAll(EntityGroupStorage.loadTransient(fileName, config));
								
//								for(Entity e : group.entities())
//								{
//									Transform2DComponent transform = Transform2DComponent.components.get(e);
//									if(transform != null) transform.position.add(x * map.worldTileWidth, y * map.worldTileHeight);
//									else Gdx.app.error("TileMap", "Transform2D not found in " + fileName);
//								}
								
								map.entityMap[y * map.width + x] = group;
								
								for(Entity e : group.entities()){
									BTreeModel btree = BTreeModel.components.get(e);
									if(btree != null){
										btree.enabled = true;
										btree.remove = true;
									}
								}
								
							}
						}
					}
					
				}else{
					// object layer
					MapObject camera = layer.getObjects().get("camera");
					Rectangle camRect = ((RectangleMapObject)camera).getRectangle();
					Vector2 camCenter = camRect.getCenter(new Vector2()).scl(map.worldTileWidth / map.tileWidth, map.worldTileHeight / map.tileHeight);
//					game.camera.position.set(new Vector3(camCenter, game.camera.position.z));
//					game.camera.update(true);
					ofx = -camCenter.x;
					ofy = -camCenter.y;
				}
			}
			
			for(int y=0 ; y<map.height ; y++){
				for(int x=0 ; x<map.width ; x++){
					EntityGroup eg =  map.entityMap[y * map.width + x];
					if(eg != null){
						for(Entity e : eg.entities())
						{
							Transform2DComponent transform = Transform2DComponent.components.get(e);
							if(transform != null) transform.position.add(ofx + x * map.worldTileWidth, ofy + y * map.worldTileHeight);
							else Gdx.app.error("TileMap", "Transform2D not found at " + x + ", " + y);
						}
					}
				}
			}
			
		}
		
		// update from camera settings and extra range lookup
//		float depth = game.camera.project(new Vector3()).z;
//		Vector3 min = game.camera.unproject(new Vector3(-Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, depth));
//		Vector3 max = game.camera.unproject(new Vector3(Gdx.graphics.getWidth()/2, -Gdx.graphics.getHeight()/2, depth));
		
//		int xOffset = MathUtils.floor(min.x / map.worldTileWidth);
//		int yOffset = MathUtils.floor(min.y / map.worldTileHeight);
//		
//		int xSize = MathUtils.ceil((max.x - min.x) / map.worldTileWidth);
//		int ySize = MathUtils.ceil((max.y - min.y) / map.worldTileHeight);
		
//		if(map.gridWidth < xSize || map.gridHeight < xSize){
//			// TODO enlarge grid ...
//		}
		
		
	}
	
}
