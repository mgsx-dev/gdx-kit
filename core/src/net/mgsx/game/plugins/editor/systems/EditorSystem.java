package net.mgsx.game.plugins.editor.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.plugins.EngineEditor;

public class EditorSystem extends EntitySystem
{
	// TODO visibility package
	final public ObjectMap<String, EngineEditor> globalEditors = new ObjectMap<String, EngineEditor>();
	
	public void addGlobalEditor(String name, EngineEditor factory) {
		globalEditors.put(name, factory);
	}
	
	

}
