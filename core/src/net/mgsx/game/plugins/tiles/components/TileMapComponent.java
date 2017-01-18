package net.mgsx.game.plugins.tiles.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.utils.Pool.Poolable;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.storage.EntityGroup;

// XXX @Storable("tiles.map")
@EditableComponent(autoClone=true, autoTool=false)
public class TileMapComponent implements Component, Poolable
{
	
	public final static ComponentMapper<TileMapComponent> components = ComponentMapper.getFor(TileMapComponent.class);
	
	public Map map;
	
	public EntityGroup [] entityMap;
	public int tileWidth, tileHeight, width, height;
	public float worldTileWidth, worldTileHeight;
	public String defaultTile;

	public int gridWidth, gridHeight;

	@Override
	public void reset() {
		map = null;
		entityMap = null;
	}
	
}
