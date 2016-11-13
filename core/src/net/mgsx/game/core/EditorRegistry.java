package net.mgsx.game.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.helpers.TypeMap;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.plugins.Plugin;

public class EditorRegistry extends GameRegistry
{
	protected Map<Class, Array<EntityEditorPlugin>> editablePlugins = new HashMap<Class, Array<EntityEditorPlugin>>();
	protected TypeMap<EditorPlugin> editorPlugins = new TypeMap<EditorPlugin>();
	public Map<String, GlobalEditorPlugin> globalEditors = new LinkedHashMap<String, GlobalEditorPlugin>();
	
	@Override
	public void registerPlugin(Plugin plugin) 
	{
		super.registerPlugin(plugin);
		
		if(plugin instanceof EditorPlugin)
		{
			EditorPlugin editorPlugin = (EditorPlugin)plugin;
			editorPlugins.put(editorPlugin.getClass(), editorPlugin);
		}
	}
	
	public void addGlobalEditor(String name, GlobalEditorPlugin plugin) 
	{
		globalEditors.put(name, plugin);
	}

	public <T> void registerPlugin(Class<T> type, EntityEditorPlugin plugin) 
	{
		Array<EntityEditorPlugin> plugins = editablePlugins.get(type);
		if(plugins == null) editablePlugins.put(type, plugins = new Array<EntityEditorPlugin>());
		plugins.add(plugin);
	}
	
	public void init(EditorScreen editor) 
	{
		for(EditorPlugin plugin : editorPlugins.values()){
			plugin.initialize(editor);
		}
	}
	
	@Override
	public void register(Class<? extends Component> type) 
	{
		super.register(type);
		
	}

}
