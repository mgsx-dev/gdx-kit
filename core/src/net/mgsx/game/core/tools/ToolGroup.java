package net.mgsx.game.core.tools;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.utils.Array;

public class ToolGroup extends InputMultiplexer
{
	public static interface ToolGroupHandler
	{
		public void onToolChanged(Tool tool);
	}
	
	final public Array<Tool> tools = new Array<Tool>();

	private Tool activeTool, defaultTool;
	
	private ButtonGroup<Button> group;

	protected ToolGroupHandler handler;
	
	public ToolGroup(ToolGroupHandler handler) {
		this.handler = handler;
		group = new ButtonGroup<Button>();
		group.setMinCheckCount(0);
	}
	
	public void setDefaultTool(Tool defaultTool) {
		this.defaultTool = defaultTool;
	}
	
	public void setActiveTool(Tool tool){
		if(activeTool != null){
			activeTool.desactivate();
			removeProcessor(activeTool);
			handler.onToolChanged(null);
			activeTool.group = null;
		}
		activeTool = tool;
		if(activeTool != null){
			activeTool.group = this;
			handler.onToolChanged(activeTool);
			addProcessor(activeTool);
			activeTool.activate();
		}else{
			group.uncheckAll();
		}
	}

	public void render(ShapeRenderer renderer) {
		if(activeTool != null) activeTool.render(renderer);
	}

	final void end(Tool tool) {
		setActiveTool(defaultTool);
	}

	public void addButton(Button btTool) {
		group.add(btTool);
	}
	
	public void clearButtons(){
		group.clear();
	}
	
	@Override
	public void clear() {
		setActiveTool(null);
		clearButtons();
		super.clear();
	}

	public void render(SpriteBatch batch) {
		if(activeTool != null) activeTool.render(batch);
	}

	public void update(float deltaTime) {
		if(activeTool != null) activeTool.update(deltaTime);
	}

	public void removeButton(Button button) {
		group.remove(button);
	}
	
}
