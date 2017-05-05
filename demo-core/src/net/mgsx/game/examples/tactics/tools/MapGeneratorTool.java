package net.mgsx.game.examples.tactics.tools;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EnumType;
import net.mgsx.game.core.storage.EntityGroup;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.tiles.components.TileMapComponent;

public class MapGeneratorTool extends Tool
{
	@Editable(type=EnumType.RANDOM)
	public long seed = MathUtils.random(Long.MAX_VALUE);
	
	@Editable public int tileHeight = 32;
	@Editable public int tileWidth = 32;
	@Editable public int height = 32;
	@Editable public int width = 32;
	@Editable public long xOffset = 0;
	@Editable public long yOffset = 0;

	public MapGeneratorTool(EditorScreen editor) {
		super(editor);
	}
	
	@Override
	protected void activate() 
	{
		super.activate();
		
		currentEntity();
		
		TileMapComponent tmc = new TileMapComponent();
		tmc.entityMap = new EntityGroup[]{};
		tmc.map = new TiledMap();
		TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
		tmc.map.getLayers().add(layer);
		// TODO gen map
		
		build();
		
		// TODO ... get region from a tileset !
		StaticTiledMapTile groundTile = new StaticTiledMapTile(new TextureRegion());
		StaticTiledMapTile waterTile = new StaticTiledMapTile(new TextureRegion());
		
		// rasterization
		for(int y=0 ; y<height ; y++)
		{
			for(int x=0 ; x<width ; x++)
			{
				float rndWater = lookup(0, x, y);
				
				TiledMapTile tile;
				if(rndWater < 0.5f){
					tile = new StaticTiledMapTile(waterTile);
				}else{
					tile = new StaticTiledMapTile(groundTile);
				}
				layer.setCell(x, y, new Cell().setTile(tile));
			}
		}
		
	}
	
	private float[][][] tables;
	private int layers = 8;
	
	private void build() {
		RandomXS128 random = new RandomXS128(seed);
		
		tables = new float[height][][];
		for(int y=0 ; y<height ; y++)
		{
			float[][] cols = tables[y] = new float[width][];
			for(int x=0 ; x<width ; x++)
			{
				// locate seed on 2D plane
				random.setSeed(seed + y);
				random.setSeed(random.nextInt() + x);
				
				float[] cell  = cols[x] = new float[layers];
				
				float fx = x / (float)width;
				float fy = y / (float)height;
				
				for(int i=0 ; i<layers ; i++){
					cell[i] = random.nextFloat();
				}
				
			}
		}
	}

	private float lookup(int i, int x, int y) 
	{
		return tables[y][x][i];
	}
	
}
