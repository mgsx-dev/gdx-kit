package net.mgsx.game.core.tools;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.ui.EntityEditor;

public class ToolGroup extends InputMultiplexer
{
	final public Array<Tool> tools = new Array<Tool>();

	private Tool activeTool, defaultTool;
	
	private ButtonGroup<Button> group;

	protected EditorScreen editor;
	
	public ToolGroup(EditorScreen editor) {
		this.editor = editor;
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
			editor.toolOutline.clear();
		}
		activeTool = tool;
		if(activeTool != null){
			activeTool.group = this;
			addProcessor(activeTool);
			
			// TODO handled by editor 
			Table table = new Table(editor.skin);
			Table view = new EntityEditor(activeTool, true, editor.skin);
			table.setBackground(editor.skin.getDrawable("default-rect"));
			table.add(activeTool.name).row();
			table.add(view).row();
			editor.toolOutline.clear();
			editor.toolOutline.add(table);
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
