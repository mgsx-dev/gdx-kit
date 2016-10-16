package net.mgsx.core.tools;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.Editor;

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
	final protected Editor editor;
	ToolGroup group;
	
	public Family activator;
	
	public Tool(Editor editor) {
		this("no name", editor);
	}
	public Tool(String name, Editor editor) {
		super();
		this.editor = editor;
		this.name = name;
		camera = editor.orthographicCamera;
	}

	final protected void end(){
		group.end(this);
	}

	protected void activate(){}
	protected void desactivate(){}
	
	/**
	 * helper
	 * @return
	 */
	protected static final boolean ctrl(){
		return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

	}
	/**
	 * helper
	 * @return
	 */
	protected static final boolean shift(){
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

	}

	protected Vector2 unproject(float screenX, float screenY) {
		Vector3 v = camera.unproject(new Vector3(screenX, screenY, 0));
		return new Vector2(v.x, v.y);
	}
	protected Vector2 unproject(Vector2 screenPosition) {
		return unproject(screenPosition.x, screenPosition.y);
	}
	protected Vector2 project(Vector2 worldPosition) {
		Vector3 v = camera.project(new Vector3(worldPosition.x, worldPosition.y, 0));
		return new Vector2(v.x, v.y);
	}
	
	protected Camera camera;
	
	final public String name;

	public void render(ShapeRenderer renderer){
		
	}
	public void render(Batch batch){
		
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
		Vector3 scale = camera.combined.getScale(new Vector3()).scl(0.5f); // TODO why 2 ?
		return new Vector2(1.f / (scale.x * Gdx.graphics.getWidth()), 1.f/(scale.y * Gdx.graphics.getHeight()));
		
	}
	
	
}
