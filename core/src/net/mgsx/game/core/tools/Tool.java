package net.mgsx.game.core.tools;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.Editor;

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
abstract public class Tool extends InputAdapter
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
		return unproject(editor.camera, screenX, screenY);
	}
	public static Vector2 unproject(Camera camera, float screenX, float screenY) {
		Vector3 base = camera.project(new Vector3());
		Vector3 v = camera.unproject(new Vector3(screenX, screenY, base.z));
		return new Vector2(v.x, v.y);
	}
	protected Vector2 unproject(Vector2 screenPosition) {
		return unproject(screenPosition.x, screenPosition.y);
	}
	protected Vector2 project(Vector2 worldPosition) {
		Vector3 v = editor.camera.project(new Vector3(worldPosition.x, worldPosition.y, 0));
		return new Vector2(v.x, v.y);
	}
	
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
		return pixelSize(editor.camera);
	}
	public static Vector2 pixelSize(Camera camera)
	{
		// that was the old method for orthographic camera
		// TODO do the same for perspective as well.
		
		Vector3 base = camera.project(new Vector3());
		
		Vector3 a = camera.unproject(new Vector3(0,0,base.z));
		Vector3 b = camera.unproject(new Vector3(1,1,base.z));
		b.sub(a);
		
		
//		Vector3 scale = camera.combined.getScale(new Vector3());
//		return new Vector2(scale.x, scale.y).scl(1);
//		return new Vector2(Gdx.graphics.getWidth() * scale.x, scale.y / Gdx.graphics.getHeight());
		return new Vector2(b.x, b.y);
		
//		Vector3 scale = camera.combined.getScale(new Vector3()).scl(0.5f); // TODO why 2 ?
//		return new Vector2(1.f / (scale.x * Gdx.graphics.getWidth()), 1.f/(scale.y * Gdx.graphics.getHeight()));
		
	}
	
	
}
