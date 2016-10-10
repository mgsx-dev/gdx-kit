package net.mgsx.fwk.editor;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class ToolGroup extends InputMultiplexer
{
	final public Array<Tool> tools = new Array<Tool>();

	private Tool activeTool, defaultTool;
	
	public void setDefaultTool(Tool defaultTool) {
		this.defaultTool = defaultTool;
	}
	
	public void setActiveTool(Tool tool){
		if(activeTool != null){
			activeTool.group = null;
			removeProcessor(activeTool);
		}
		activeTool = tool;
		if(activeTool != null){
			activeTool.group = this;
			addProcessor(activeTool);
		}
	}

	public void render(ShapeRenderer renderer) {
		if(activeTool != null) activeTool.render(renderer);
	}

	final void end(Tool tool) {
		if(defaultTool != null) setActiveTool(defaultTool);
	}
}
