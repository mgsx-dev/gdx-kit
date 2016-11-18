package net.mgsx.game.core.tools;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

public class ToolGroup extends InputMultiplexer
{
	final public Array<Tool> tools = new Array<Tool>();

	private Tool activeTool, defaultTool;
	
	private ButtonGroup<Button> group;
	
	public ToolGroup() {
		group = new ButtonGroup<Button>();
		group.setMinCheckCount(0);
	}
	
	public void setDefaultTool(Tool defaultTool) {
		this.defaultTool = defaultTool;
	}
	
	public void setActiveTool(Tool tool){
		if(activeTool != null){
			activeTool.desactivate();
			activeTool.group = null;
			removeProcessor(activeTool);
		}
		activeTool = tool;
		if(activeTool != null){
			activeTool.group = this;
			addProcessor(activeTool);
			activeTool.activate();
		}
	}

	public void render(ShapeRenderer renderer) {
		if(activeTool != null) activeTool.render(renderer);
	}

	final void end(Tool tool) {
		if(defaultTool != null) setActiveTool(defaultTool);
	}

	public void addButton(TextButton btTool) {
		group.add(btTool);
	}
	
	public void clearButtons(){
		group.clear();
	}
	
	@Override
	public void clear() {
		clearButtons();
		activeTool = defaultTool = null;
		super.clear();
	}

	public void render(SpriteBatch batch) {
		if(activeTool != null) activeTool.render(batch);
	}

	public void update(float deltaTime) {
		if(activeTool != null) activeTool.update(deltaTime);
	}
	
}
