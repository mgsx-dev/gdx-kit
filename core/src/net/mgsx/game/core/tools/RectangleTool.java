package net.mgsx.game.core.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;

abstract public class RectangleTool extends Tool
{
	protected Vector2 startPoint, endPoint;
	protected int buttonFilter = Input.Buttons.LEFT;
	
	public RectangleTool(String name, EditorScreen editor) {
		super(name, editor);
	}
	
	@Override
	protected void activate() {
		super.activate();
		setInfo("Draw a rectangle by dragging mouse.");
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == buttonFilter){
			startPoint = snap(unproject(screenX, screenY));
			begin(startPoint);
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(startPoint != null && Gdx.input.isButtonPressed(buttonFilter)){
			endPoint = snap(unproject(screenX, screenY));
		}
		return super.touchDragged(screenX, screenY, pointer);
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == buttonFilter && startPoint != null && endPoint != null){
			create(startPoint, endPoint);
		}
		startPoint = endPoint = null;
		return super.touchUp(screenX, screenY, pointer, button);
	}
	protected abstract void create(Vector2 startPoint, Vector2 endPoint);
	protected void begin(Vector2 startPoint){}
	
	@Override
	public void render(ShapeRenderer renderer) {
		if(startPoint != null && endPoint != null){
			float x = Math.min(startPoint.x, endPoint.x);
			float y = Math.min(startPoint.y, endPoint.y);
			float w = Math.max(0, Math.abs(startPoint.x - endPoint.x));
			float h = Math.max(0, Math.abs(startPoint.y - endPoint.y));
			renderer.begin(ShapeType.Line);
			renderer.rect(x,y,w,h);
			renderer.end();
		}
	}
}
