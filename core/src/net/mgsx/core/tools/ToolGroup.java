package net.mgsx.core.tools;

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
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean result = super.touchDown(screenX, screenY, pointer, button);
		if(result == true) System.out.println(this.activeTool);
		return result;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		
		boolean result = super.touchDragged(screenX, screenY, pointer);
		if(result == true) System.out.println(this.activeTool);
		return result;
	}
}
