package net.mgsx.game.plugins.core.tools;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.tools.Tool;

@Editable
public class ClipPlaneTool extends Tool
{
	
	@Editable
	public float getFar(){
		return editor.getGameCamera().far;
	}
	@Editable
	public void setFar(float value){
		editor.getGameCamera().far = value;
		editor.getGameCamera().update();
	}

	public ClipPlaneTool(EditorScreen editor) {
		super("Camera Clip", editor);
	}

}
