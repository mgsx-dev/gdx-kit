package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.systems.GridDebugSystem;

public class GridTool extends Tool
{
	public GridTool(EditorScreen editor) {
		super("Grid", editor);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		GridDebugSystem grid = getEngine().getSystem(GridDebugSystem.class);
		if(keycode == Input.Keys.G){
			grid.setProcessing(!grid.checkProcessing());
			return true;
		}else if(keycode == Input.Keys.S){
			grid.snap = !grid.snap;
			return true;
		}
		return super.keyDown(keycode);
	}
}
