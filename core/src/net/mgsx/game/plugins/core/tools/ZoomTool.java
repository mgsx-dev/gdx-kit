package net.mgsx.game.plugins.core.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.tools.Tool;

// TODO choose between ortho and perspective ... should be synchronized in some way ...
public class ZoomTool extends Tool
{
	private Vector2 prev; //, originScreen; //, originWorld;
	
	public ZoomTool(EditorScreen editor) {
		super("Zoom", editor);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE) && (ctrl() || shift()))
		{
			Vector2 pos = new Vector2(screenX, screenY);
//			camera = editor.orthographicCamera;
//			if(camera instanceof OrthographicCamera){
//				float rate = (pos.x - prev.x) - (pos.y - prev.y) + (pos.x - prev.x) * (pos.y - prev.y);
//				float ratio = 1 - rate * 0.01f;
//				((OrthographicCamera) camera).zoom *= ratio;
//				camera.update();
//				Vector2 newWorldOrigin = unproject(originScreen);
//				Vector2 deltaWorld = new Vector2(newWorldOrigin).sub(originWorld);
//				camera.translate(-deltaWorld.x, -deltaWorld.y, 0);
//				camera.update();
//			}
			
			float rate = (pos.x - prev.x) / Gdx.graphics.getWidth(); // - (pos.y - prev.y) + (pos.x - prev.x) * (pos.y - prev.y);
			Vector2 worldPos = new Vector2(screenX, screenY);
//			Vector2 delta = new Vector2(worldPos).sub(prev).scl(pixelSize());
			
			
			if(!ctrl() && shift()){
				editor.getEditorCamera().camera().rotate(rate * 360, 0, 1, 0);
				editor.getEditorCamera().camera().update(false);
			}else if(ctrl() && shift())
				editor.getEditorCamera().fov(rate);
			else if(ctrl())
				editor.getEditorCamera().zoom(rate);
			else
				return false;
			
			
			
			
			prev = worldPos;
			
			
			
//			editor.orthographicCamera.translate(0, 0, -rate * 0.01f);
//			editor.orthographicCamera.zoom = camera.view.getScaleZ(); //position.z -5;
			
			
			
			// TODO from http://gamedev.stackexchange.com/questions/67692/how-do-i-ensure-that-perspective-and-orthographic-projection-matricies-show-obje
//			Vector3 v = new Vector3(0,0,0);
//			camera.unproject(v);
//			editor.orthographicCamera.zoom = -0.8f;
//			editor.orthographicCamera.viewportWidth = (2.f /  camera.view.val[Matrix4.M00]) * -Math.abs(v.z);
//			editor.orthographicCamera.viewportHeight = (2.f /  camera.view.val[Matrix4.M11]) * -Math.abs(v.z);
//			editor.orthographicCamera.update(true);

			
			//			float rate = (pos.x - prev.x) - (pos.y - prev.y) + (pos.x - prev.x) * (pos.y - prev.y);
//			float ratio = 1 - rate * 100;
//			editor.perspectiveCamera.position.x += ratio; // translate(0, 0, ratio);
//			editor.perspectiveCamera.update(true);
			prev = pos;
			return true;
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.MIDDLE && (ctrl() || shift()))
		{
			prev = new Vector2(screenX, screenY);
//			originScreen = new Vector2(screenX, screenY);
//			originWorld = unproject(originScreen);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
}
