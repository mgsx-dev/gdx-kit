package net.mgsx.fwk.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class ZoomTool extends Tool
{
	private Vector2 prev, originScreen, originWorld;
	
	public ZoomTool(Camera camera) {
		super("Pan", camera);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE) && ctrl())
		{
			Vector2 pos = new Vector2(screenX, screenY);
			if(camera instanceof OrthographicCamera){
				float rate = (pos.x - prev.x) - (pos.y - prev.y) + (pos.x - prev.x) * (pos.y - prev.y);
				float ratio = 1 - rate * 0.01f;
				((OrthographicCamera) camera).zoom *= ratio;
				camera.update();
				Vector2 newWorldOrigin = unproject(originScreen);
				Vector2 deltaWorld = new Vector2(newWorldOrigin).sub(originWorld);
				camera.translate(-deltaWorld.x, -deltaWorld.y, 0);
				camera.update();
			}
			prev = pos;
			return true;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.MIDDLE && ctrl())
		{
			prev = new Vector2(screenX, screenY);
			originScreen = new Vector2(screenX, screenY);
			originWorld = unproject(originScreen);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
}
