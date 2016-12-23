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
		if(keycode == Input.Keys.G){
			GridDebugSystem grid = getEngine().getSystem(GridDebugSystem.class);
			grid.setProcessing(!grid.checkProcessing());
		}
		return super.keyDown(keycode);
	}
}
