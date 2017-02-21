package net.mgsx.game.plugins.assets.editors;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.helpers.EditorAssetManager.AssetManagerListener;
import net.mgsx.game.core.plugins.EngineEditor;
import net.mgsx.game.core.ui.ListView;

public class AssetsEditor implements EngineEditor
{
	/**
	 * model for displayed asset in gui
	 */
	private static class AssetItem{

		public Class type;
		public Array<String> dependencies;
		public int references;
		public String name;
		
	}
	
	private AssetItem createItem(EditorAssetManager assets, String name){
		AssetItem item = new AssetItem();
		item.name = name;
		item.type = assets.getAssetType(name);
		item.dependencies = assets.getDependencies(name);
		item.references = assets.getReferenceCount(name);
		return item;
	}
	
	@Override
	public Actor createEditor(final Engine engine, final AssetManager assets, final Skin skin)
	{
		return createEditor(engine, (EditorAssetManager)assets, skin);
	}
	
	public Actor createEditor(final Engine engine, final EditorAssetManager assets, final Skin skin)
	{
		final ListView<AssetItem> assetList = new ListView<AssetItem>(createItems(assets), skin) {
			
			@Override
			protected void createItem(Table parent, final AssetItem item) {
				parent.add(item.name);
				parent.add(item.type.getSimpleName());
				parent.add(String.valueOf(item.dependencies == null ? "-" : item.dependencies.size));
				parent.add(String.valueOf(item.references));
				
				TextButton btReload = new TextButton("Reload", skin);
				btReload.addListener(new ChangeListener(){
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						assets.reload(item.name);
						
					}
				});
				parent.add(btReload);
			}
			
			@Override
			protected void createHeader(Table parent) {
				parent.add("Name");
				parent.add("Type");
				parent.add("Deps");
				parent.add("Refs");
				parent.add("Reload");
			}
		};
		
		final AssetManagerListener listener = new AssetManagerListener() {
			
			@Override
			public void added(String fileName, Class type) {
				assetList.addItem(createItem(assets, fileName));
			}

			@Override
			public void removed(String fileName) {
				assetList.setItems(createItems(assets));
			}

			@Override
			public void changed(String fileName) {
				assetList.setItems(createItems(assets));
			}
		};
		assets.addListener(listener);
		
		Table table = new Table(skin){
			@Override
			public void clearListeners() {
				super.clearListeners();
				assets.removeListener(listener);
			}
		};
		
		
		TextButton btRefresh = new TextButton("Refresh", skin);
		btRefresh.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				assetList.setItems(createItems(assets));
			}
		});
		
		table.add(btRefresh).row();
		//table.add(editor.assets.getDiagnostics()).row();
		//table.add(String.valueOf(editor.assets.getLoadedAssets())).row();
		table.add(new ScrollPane(assetList));
		
		return table;
	}

	protected Array<AssetItem> createItems(EditorAssetManager assets) {
		Array<AssetItem> items = new Array<AssetsEditor.AssetItem>();
		
		for(String name : assets.getAssetNames()){
			items.add(createItem(assets, name));
		}
		
		return items;
	}

}
