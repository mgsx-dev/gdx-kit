package net.mgsx.game.examples.video;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.plugins.EditorPlugin;
import net.mgsx.game.examples.video.tools.VideoTool;
import net.mgsx.game.plugins.DefaultEditorPlugin;

public class VideoExamplePlugin extends EditorPlugin implements DefaultEditorPlugin
{

	@Override
	public void initialize(EditorScreen editor) {
		editor.addTool(editor.defaultTool = new VideoTool(editor));
	}
	
}