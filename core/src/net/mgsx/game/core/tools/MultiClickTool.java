package net.mgsx.game.core.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.EditorScreen;

/**
 * This tool is used as follow :
 * - user left click multiple times
 * - right click to complete
 * - escape to abort
 *  
 */
abstract public class MultiClickTool extends Tool
{
	private boolean running;
	final protected Array<Vector2> dots = new Array<Vector2>();
	protected int maxPoints;
	protected abstract void complete();
	protected void abort(){}
	
	public MultiClickTool(String name, EditorScreen editor) {
		this(name, editor, -1);
	}
	public MultiClickTool(String name, EditorScreen editor, int maxPoints) {
		super(name, editor);
		this.maxPoints = maxPoints;
	}
	
	protected boolean isRunning() {
		return running;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			Vector2 worldPosition = snap(unproject(screenX, screenY));
			dots.add(worldPosition);
			onNewDot(worldPosition);
			running = true;
			if(maxPoints >= 0 && dots.size >= maxPoints){
				running = false;
				complete();
				dots.clear();
			}
			return true;
		}
		// right click to complete (when no limits)
		else if(maxPoints < 0 && button == Input.Buttons.RIGHT){
			running = false;
			complete();
			dots.clear();
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
	protected void onNewDot(Vector2 worldPosition) {}
	
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.ESCAPE){
			running = false;
			abort();
			return true;
		}
		return super.keyDown(keycode);
	}
	
	@Override
	public void render(ShapeRenderer renderer) 
	{
		Vector2 s = pixelSize().scl(8);
		renderer.begin(ShapeType.Filled);
		for(Vector2 dot : dots){
			renderer.rect(dot.x-s.x, dot.y-s.y, 2*s.x, 2*s.y);
		}
		renderer.end();
	}
	
}
