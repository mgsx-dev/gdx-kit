package net.mgsx.game.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;

import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.helpers.TypeMap;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.core.plugins.EntityEditorPlugin;
import net.mgsx.game.core.plugins.Plugin;

public class EditorRegistry extends GameRegistry
{
	protected Map<Class, Array<EntityEditorPlugin>> editablePlugins = new HashMap<Class, Array<EntityEditorPlugin>>();
	protected TypeMap<EditorPlugin> editorPlugins = new TypeMap<EditorPlugin>();
	protected ObjectMap<String, String> packagePrefixToTag = new ObjectMap<String, String>();
	protected ObjectMap<Object, String> objectTags = new ObjectMap<Object, String>();
	
	@Override
	public boolean registerPlugin(Plugin plugin) 
	{
		if(!super.registerPlugin(plugin)){
			return false;
		}
		
		if(plugin instanceof EditorPlugin && !editablePlugins.containsKey(plugin.getClass()))
		{
			EditorPlugin editorPlugin = (EditorPlugin)plugin;
			editorPlugins.put(editorPlugin.getClass(), editorPlugin);
			
		}
		// add package prefix if not overridden by caller.
		String packageName = plugin.getClass().getPackage().getName();
		if(!packagePrefixToTag.containsKey(packageName)){
			String [] folders = packageName.split("\\.");
			String name;
			PluginDef def = plugin.getClass().getAnnotation(PluginDef.class);
			if(def != null && !def.category().isEmpty()){
				name = def.category();
			}else{
				name = folders[folders.length-1];
			}
			packagePrefixToTag.put(packageName, name);
		}
		return true;
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
	
	public Array<String> allTags() 
	{
		// TODO optimize by caching on configuration phase end.
		ObjectSet<String> set = new ObjectSet<String>();
		set.addAll(packagePrefixToTag.values().toArray());
		Array<String> r = new Array<String>();
		for(String s : set) r.add(s);
		return r;
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
