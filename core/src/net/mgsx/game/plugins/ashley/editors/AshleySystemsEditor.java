package net.mgsx.game.plugins.ashley.editors;

import java.lang.reflect.Field;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.plugins.EngineEditor;
import net.mgsx.game.core.storage.LoadConfiguration;
import net.mgsx.game.core.ui.EntityEditor;
import net.mgsx.game.core.ui.accessors.MethodAccessor;
import net.mgsx.game.core.ui.events.EditorListener;
import net.mgsx.game.plugins.editor.systems.EditorSystem;

public class AshleySystemsEditor implements EngineEditor
{
	private String pluginFilter;
	
	@Override
	public Actor createEditor(final Engine engine, AssetManager assets, final Skin skin) 
	{
		final Table table = new Table(skin);
		table.setBackground(skin.getDrawable("default-window-body-right"));
		
		EditorSystem editorSystem = engine.getSystem(EditorSystem.class);
		
		rebuildUI(engine, table, skin);
		
		editorSystem.addListener(new EditorListener(){
			@Override
			public void onLoad(LoadConfiguration cfg) {
				rebuildUI(engine, table, skin);
			}
		});
		
		return new ScrollPane(table, skin, "light");
	}
	
	private Actor createFilterBox(final Engine engine, final Table table, final Skin skin)
	{
		EditorSystem editorSystem = engine.getSystem(EditorSystem.class);
		
		final SelectBox<String> pluginFilterBox = new SelectBox<String>(skin);
		Array<String> allPlugins = new Array<String>();
		allPlugins.add("");
		allPlugins.addAll(editorSystem.registry.allTags());
		allPlugins.sort();
		pluginFilterBox.setItems(allPlugins);
		pluginFilterBox.setSelected(pluginFilter == null ? "" : pluginFilter);
		pluginFilterBox.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				pluginFilter = pluginFilterBox.getSelected();
				rebuildUI(engine, table, skin);
			}
		});
		return pluginFilterBox;
	}
	
	private void rebuildUI(final Engine engine, final Table table, final Skin skin) 
	{
		final EditorSystem editorSystem = engine.getSystem(EditorSystem.class);
		
		table.clearChildren();
		
		TextButton btRefresh = new TextButton("refresh", skin);
		
		btRefresh.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				rebuildUI(engine, table, skin);
			}
		});
		
		table.add("System");
		table.add("Priority");
		table.add("Entities");
		table.add("Processing");
		table.add("Edition");
		table.row();
		table.add().height(10);
		table.row();
		
		
		// table with 5 columns
		table.add(createFilterBox(engine, table, skin)).fill();
		table.add(); // TODO priority filter ? sorter ?
		table.add(btRefresh);
		table.add(); // TODO process all ? filter processing / not processing ?
		table.add(); // TODO edit all ? undock all ?
		table.row();

		
		ObjectMap<Integer, String> pipelineLegend = new ObjectMap<Integer, String>();
		for(Field field : GamePipeline.class.getFields()){
			Integer priority = (Integer)ReflectionHelper.get(null, field);
			if(priority != null) pipelineLegend.put(priority, field.getName());
		}
		for(final EntitySystem system : engine.getSystems())
		{
			// filter systems
			if(pluginFilter != null && !pluginFilter.isEmpty())
			{
				if(!pluginFilter.equals(editorSystem.registry.getTag(system))){
					continue;
				}
			}
			
			
			String priorityName = pipelineLegend.get(system.priority);
			if(priorityName == null) priorityName = "undefined (" + String.valueOf(system.priority) + ")";
			
			String description = "";
			if(system instanceof IteratingSystem){
				int size = ((IteratingSystem) system).getEntities().size();
				description += String.valueOf(size);
			}else{
				description = "-";
			}
			
			Actor edit;
			EditableSystem config = system.getClass().getAnnotation(EditableSystem.class);
			if(config != null){
				edit = new TextButton("edit", skin);
				edit.addListener(new ChangeListener(){
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						editorSystem.pinEditor(system);
					}
				});
			}else{
				edit = new Label("-", skin);
			}
			
			table.add(name(system)).left();
			table.add(priorityName);
			table.add(description);
			
			if(config == null || config.allowSetProcessing())
				EntityEditor.createControl(table, system, new MethodAccessor(system, "processing", "checkProcessing", "setProcessing"));
			else
				table.add(String.valueOf(system.checkProcessing()));
			table.add(edit);
			table.row();
		}
	}
	
	private String name(EntitySystem system){
		String systemName;
		EditableSystem config = system.getClass().getAnnotation(EditableSystem.class);
		if(config == null || config.value().isEmpty()){
			systemName = system.getClass().getSimpleName();
			if(system.getClass().getEnclosingClass() != null){
				systemName = system.getClass().getEnclosingClass().getSimpleName();
			}
		}else{
			systemName = config.value();
		}
		return systemName;
	}


}
