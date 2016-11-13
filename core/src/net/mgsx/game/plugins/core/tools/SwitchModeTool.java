package net.mgsx.game.plugins.core.tools;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.tools.Tool;

// TODO separate switch mode and camera ? add a GUI for it ?
public class SwitchModeTool extends Tool
{
	private boolean displayEnabled = true;

	public SwitchModeTool(EditorScreen editor) {
		super(editor);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.TAB){
			displayEnabled  = !displayEnabled;
			for(EntitySystem system : editor.entityEngine.getSystems())
			{
				if(system.priority == GamePipeline.RENDER_OVER) system.setProcessing(displayEnabled);
			}
			return true;
		}
		else if(keycode == Input.Keys.F3)
		{
			editor.switchCamera();
		}
		return super.keyDown(keycode);
	}
	
}
