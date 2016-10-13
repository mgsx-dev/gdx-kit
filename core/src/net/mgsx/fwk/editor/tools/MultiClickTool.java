package net.mgsx.fwk.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

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
	
	public MultiClickTool(String name, Camera camera) {
		this(name, camera, -1);
	}
	public MultiClickTool(String name, Camera camera, int maxPoints) {
		super(name, camera);
		this.maxPoints = maxPoints;
	}
	
	protected boolean isRunning() {
		return running;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == Input.Buttons.LEFT){
			Vector3 worldPosition = camera.unproject(new Vector3(screenX, screenY, 0));
			dots.add(new Vector2(worldPosition.x, worldPosition.y));
			running = true;
			if(maxPoints >= 0 && dots.size >= maxPoints){
				running = false;
				complete();
				end();
				dots.clear();
			}
			return true;
		}
		// right click to complete (when no limits)
		else if(maxPoints < 0 && button == Input.Buttons.RIGHT){
			running = false;
			complete();
			end();
			dots.clear();
			return true;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}
	
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
