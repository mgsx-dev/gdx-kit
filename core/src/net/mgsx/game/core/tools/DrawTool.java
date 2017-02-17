package net.mgsx.game.core.tools;

import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;

abstract public class DrawTool extends Tool
{
	private Vector2 current = new Vector2();
	
	public DrawTool(String name, EditorScreen editor) {
		super(name, editor);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) 
	{
		unproject(current, screenX, screenY);
		drawingStart(current);
		return true;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		unproject(current, screenX, screenY);
		drawing(current);
		return true;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		unproject(current, screenX, screenY);
		drawingEnd(current);
		return true;
	}
	
	abstract protected void drawingStart(Vector2 position);
	abstract protected void drawing(Vector2 position);
	abstract protected void drawingEnd(Vector2 position);


}
