package net.mgsx.game.plugins.assets.editors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.EditorAssetManager;
import net.mgsx.game.core.helpers.EditorAssetManager.AssetManagerListener;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.ui.ListView;

public class AssetsEditor implements GlobalEditorPlugin
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
	public Actor createEditor(final EditorScreen editor, final Skin skin)
	{
		final ListView<AssetItem> assetList = new ListView<AssetItem>(createItems(editor.assets), skin) {
			
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
						editor.assets.reload(item.name);
						
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
				assetList.addItem(createItem(editor.assets, fileName));
			}

			@Override
			public void removed(String fileName) {
				assetList.setItems(createItems(editor.assets));
			}

			@Override
			public void changed(String fileName) {
				assetList.setItems(createItems(editor.assets));
			}
		};
		editor.assets.addListener(listener);
		
		Table table = new Table(skin){
			@Override
			public void clearListeners() {
				super.clearListeners();
				editor.assets.removeListener(listener);
			}
		};
		
		
		TextButton btRefresh = new TextButton("Refresh", skin);
		btRefresh.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				assetList.setItems(createItems(editor.assets));
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
