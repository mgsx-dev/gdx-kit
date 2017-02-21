package net.mgsx.game.plugins.tiles.tools;

import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.NativeService;
import net.mgsx.game.core.helpers.NativeService.DefaultCallback;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.tiles.components.TileMapComponent;

public class TileMapTool extends Tool
{

	public TileMapTool(EditorScreen editor) {
		super("Entity TileMap", editor);
	}
	
	@Override
	protected void activate() {
		super.activate();
		NativeService.instance.openLoadDialog(new DefaultCallback() {
			@Override
			public void selected(FileHandle file) {
				TiledMap map = new TmxMapLoader(new AbsoluteFileHandleResolver()).load(file.path());
				TileMapComponent tmc = getEngine().createComponent(TileMapComponent.class);
				tmc.map = map;
				
				// TODO load subsequent entity groups ?
				
				currentEntity().add(tmc);
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
}
