package net.mgsx.game.plugins.editor.tools;

import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.binding.LearnSession;
import net.mgsx.game.core.tools.Tool;

public class LearnTool extends Tool
{
	private LearnSession learnSession;
	public LearnTool(EditorScreen editor) {
		super(editor);
		
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.L && ctrl() && shift()){
			learnSession = new LearnSession();
			learnSession.startLearning(editor.stage, editor.skin);
			return true;
		}
		return false;
	}
	
}
