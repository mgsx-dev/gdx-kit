package net.mgsx.game.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.helpers.ReflectionHelper;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.plugins.GlobalEditorPlugin;
import net.mgsx.game.core.plugins.Plugin;

public class EditorRegistry extends GameEngine // TODO isolation editor/engine, use Registry object instead !
{
	protected Map<Class, Array<EntityEditorPlugin>> editablePlugins = new HashMap<Class, Array<EntityEditorPlugin>>();

	protected Map<String, GlobalEditorPlugin> globalEditors = new LinkedHashMap<String, GlobalEditorPlugin>();
	
	public void registerPlugin(EditorPlugin plugin) {
		if(editorPlugins.containsKey(plugin.getClass())) return;
		PluginDef def = plugin.getClass().getAnnotation(PluginDef.class);
		if(def != null){
			for(Class<? extends Plugin> dependency : def.dependencies()){
				registerPlugin(ReflectionHelper.newInstance(dependency));
			}
		}
		editorPlugins.put(plugin.getClass(), plugin);
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

}
