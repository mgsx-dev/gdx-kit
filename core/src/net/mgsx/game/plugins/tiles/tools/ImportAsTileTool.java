package net.mgsx.game.plugins.tiles.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.storage.EntityGroupStorage;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;

public class ImportAsTileTool extends Tool {
	public ImportAsTileTool(EditorScreen editor) {
		super("Import Tiles", editor);
	}

	@Override
	protected void activate() {
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				TiledMap map = new TmxMapLoader(new AbsoluteFileHandleResolver()).load(file.path());
				importMap(map, file, editor);
				end();
			}
			@Override
			public boolean match(FileHandle file) {
				return file.extension().equals("tmx");
			}
			@Override
			public String description() {
				return "Tiledmap files (tmx)";
			}
		});
	}

	private void importMap(TiledMap map, FileHandle mapFile, EditorScreen editor){
		LoadConfiguration config = new LoadConfiguration();
		config.assets = editor.assets;
		config.registry = editor.registry;
		config.engine = editor.entityEngine;
		float res = 2;
		for(MapLayer layer : map.getLayers()){
			if(layer instanceof TiledMapTileLayer){
				TiledMapTileLayer tileLayer = ((TiledMapTileLayer) layer);
				for(int y=0 ; y<tileLayer.getHeight() ; y++){
					for(int x=0 ; x<tileLayer.getWidth() ; x++){
						Cell cell = tileLayer.getCell(x, y);
						if(cell != null){
							TiledMapTile tile = cell.getTile();
							String name = String.valueOf(tile.getProperties().get("name"));
							FileHandle file = mapFile.parent().parent().child("library").child(name + ".json");
							for(Entity entity : EntityGroupStorage.loadTransient(file.path(), config))
							{
								editor.getMovable(entity).move(entity, new Vector3(x * res, y * res, 0));
								BoundaryComponent b = entity.getComponent(BoundaryComponent.class);
								if(b != null){
									b.box.mul(new Matrix4().setTranslation(new Vector3(x * res, y * res, 0)));
								}
							}
						}
					}
					
				}
			}
		}
	}
}