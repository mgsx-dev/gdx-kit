package net.mgsx.game.plugins.editor.systems;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorRegistry;
import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EngineEditor;
import net.mgsx.game.core.tools.ToolGroup;
import net.mgsx.game.core.ui.events.EditorListener;

// FIXME this is just a wrapper to editor screen ! lot of code has to be removed ... latter
public class EditorSystem extends EntitySystem
{
	Array<ToolGroup> tools = new Array<ToolGroup>();

	// TODO visibility package
	// Use java map in order to have ordered items.
	final public Map<String, EngineEditor> globalEditors = new LinkedHashMap<String, EngineEditor>();
	
	final public EditorRegistry registry;
	
	final private EditorScreen screen;
	
	public EditorSystem(EditorScreen screen) {
		super();
		this.screen = screen;
		this.registry = screen.registry;
	}

	public void addGlobalEditor(String name, EngineEditor factory) {
		globalEditors.put(name, factory);
	}

	public void addListener(EditorListener editorListener) {
		screen.addListener(editorListener);
	}

	public void pinEditor(EntitySystem system) {
		screen.pinEditor(system);
	}

	public void addTool(ToolGroup group) {
		tools.add(group);
	}

	public void addTools(Array<ToolGroup> tools) {
		this.tools.addAll(tools);
	}

	public boolean isEditorCamera() {
		return screen.getEditorCamera().isActive();
	}
	public Camera getEditorCamera() {
		return screen.getEditorCamera().camera();
	}
	

}
