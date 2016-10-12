package net.mgsx.fwk.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * A tool may be active in an editor.
 * a tool can be a select tool, a pan tool, draw tool.
 * 
 * a tool performs command from input given (mouse, keyboard ...)
 * 
 * for instance a classic editor have at the same time :
 * - a pan tool (drag with middle click)
 * - a select tool (left click)
 * - a contextual menu (right click)
 * when user click a brush to draw, select tool is replaced by
 * a draw tool.
 * 
 * See ToolGroup : exclusive tool (eg select + draw)
 */
public class Tool extends InputAdapter
{
	protected Vector2 unproject(int screenX, int screenY) {
		Vector3 v = camera.unproject(new Vector3(screenX, screenY, 0));
		return new Vector2(v.x, v.y);
	}
	
	protected Camera camera;
	ToolGroup group;
	final public String name;
	public Tool(String name, Camera camera) {
		this.name = name;
		this.camera = camera;
	}
	public void render(ShapeRenderer renderer){
		
	}
	public void render(Batch batch){
		
	}
	final protected void end(){
		group.end(this);
	}
	/**
	 * return size of 1 pixel in camera space
	 * @return
	 */
	protected Vector2 pixelSize()
	{
		return pixelSize(camera);
	}
	public static Vector2 pixelSize(Camera camera)
	{
		Vector3 scale = camera.combined.getScale(new Vector3());
		return new Vector2(1.f / (scale.x * Gdx.graphics.getWidth()), 1.f/(scale.y * Gdx.graphics.getHeight()));
		
	}
	
	protected void activate(){}
	protected void desactivate(){}
	
	protected final boolean ctrl(){
		return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

	}
	protected final boolean shift(){
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

	}
	
}
