package net.mgsx.game.plugins.graphics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;

public class FullscreenTool extends Tool
{
	private int previousWidth = 640, previousHeight = 480;
	
	public FullscreenTool(EditorScreen editor) {
		super("Fullscreen Switch", editor);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if(ctrl() && keycode == Input.Keys.ENTER){
			if(Gdx.graphics.isFullscreen()){
				Gdx.graphics.setWindowedMode(previousWidth, previousHeight);
				// TODO Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
			}
			else{
				previousWidth = Gdx.graphics.getWidth();
				previousHeight = Gdx.graphics.getHeight();
				
				DisplayMode dm = Gdx.graphics.getDisplayMode();
				Gdx.graphics.setFullscreenMode(dm);
				// TODO Gdx.graphics.setSystemCursor(null);
			}
			return true;
		}
		return false;
	}

}
