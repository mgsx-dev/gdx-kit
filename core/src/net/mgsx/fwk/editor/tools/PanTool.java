package net.mgsx.fwk.editor.tools;

import net.mgsx.fwk.editor.Tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public class PanTool extends Tool
{
	private Vector2 prev;
	
	public PanTool(Camera camera) {
		super("Pan", camera);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE))
		{
			
			Vector2 worldPos = new Vector2(screenX, screenY);
			Vector2 delta = new Vector2(worldPos).sub(prev).scl(pixelSize()).scl(2);  // TODO why 2 ?
			camera.translate(-delta.x, delta.y, 0);
			camera.update(true);
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
