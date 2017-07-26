package net.mgsx.game.plugins.core.tools;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;

// Just same as SwitchCameraTool but available for android as well
// TODO impelments kind of binding
public class SwitchCameraTool2 extends Tool {
	public SwitchCameraTool2(EditorScreen editor) {
		super("Camera switch", editor);
	}

	@Override
	protected void activate() {
		super.activate();
		editor.getEditorCamera().switchCameras();
		end();
	}
}