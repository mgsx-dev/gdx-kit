package net.mgsx.game.examples.tactics.util;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.ObjectMap;

public class TiledMapHelper {

	public static ObjectMap<String, TiledMapTile> groupBy(Iterable<TiledMapTile> tileset, String property) {
		ObjectMap<String, TiledMapTile> map = new ObjectMap<String, TiledMapTile>();
		for(TiledMapTile tile : tileset){
			if(tile.getProperties().containsKey(property)){
				map.put(tile.getProperties().get(property).toString(), tile);
			}
		}
		return map;
	}

}
