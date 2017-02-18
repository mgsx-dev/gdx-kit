package net.mgsx.game.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

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
	protected ObjectMap<String, String> packagePrefixToTag = new ObjectMap<String, String>();
	protected ObjectMap<Object, String> objectTags = new ObjectMap<Object, String>();
	
	@Override
	public void registerPlugin(Plugin plugin) 
	{
		super.registerPlugin(plugin);
		
		if(plugin instanceof EditorPlugin && !editablePlugins.containsKey(plugin.getClass()))
		{
			EditorPlugin editorPlugin = (EditorPlugin)plugin;
			editorPlugins.put(editorPlugin.getClass(), editorPlugin);
			
			// add package prefix if not overridden by caller.
			String packageName = editorPlugin.getClass().getPackage().getName();
			if(!packagePrefixToTag.containsKey(packageName)){
				String [] folders = packageName.split("\\.");
				packagePrefixToTag.put(packageName, folders[folders.length-1]);
			}
		}
	}
	
	public void setTag(Object object, String tag) 
	{
		objectTags.put(object, tag);
	}

	public String getTagByType(Class type) 
	{
		String name = type.getPackage().getName();
		for(Entry<String, String> entry : packagePrefixToTag){
			if(name.startsWith(entry.key)){
				return entry.value;
			}
		}
		return "undefined";
	}
	public String getTag(Object object)
	{
		String tag = objectTags.get(object);
		if(tag == null){
			tag = getTagByType(object.getClass());
			objectTags.put(object, tag);
		}
		return tag;
	}
	
	public Array<String> allTags() {
		return packagePrefixToTag.values().toArray();
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
	
	@Override
	void collect(GameScreen screen) {
		// XXX prevent normal collect, wait editor systems.
	}
	
	public void init(EditorScreen editor) 
	{
		for(EditorPlugin plugin : editorPlugins.values()){
			plugin.initialize(editor);
		}
		super.collect(editor.game);
	}
}
