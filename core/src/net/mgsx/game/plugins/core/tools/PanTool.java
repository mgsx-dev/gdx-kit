package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;

public class PanTool extends Tool
{
	private Vector2 prev;
	
	public PanTool(EditorScreen editor) {
		super("Pan", editor);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(prev != null && Gdx.input.isButtonPressed(Input.Buttons.MIDDLE))
		{
			//
			Vector2 worldPos = new Vector2(screenX, screenY);
			Vector2 delta = new Vector2(worldPos).sub(prev).scl(pixelSize()); // XXX why 10 ?
			editor.getRenderCamera().translate(-delta.x, -delta.y, 0); 
			editor.getRenderCamera().update(true);
			prev = worldPos;
			return true;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.MIDDLE)
		{
			prev = new Vector2(screenX, screenY);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
}
