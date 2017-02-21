package net.mgsx.game.plugins.editor.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.plugins.GlobalEditorPlugin;

public class EditorSystem extends EntitySystem
{
	// TODO visibility package
	final public ObjectMap<String, GlobalEditorPlugin> globalEditors = new ObjectMap<String, GlobalEditorPlugin>();
	
	public void addGlobalEditor(String name, GlobalEditorPlugin factory) {
		globalEditors.put(name, factory);
	}
	
	

}
