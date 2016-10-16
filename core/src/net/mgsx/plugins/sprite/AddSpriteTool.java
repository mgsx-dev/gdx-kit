package net.mgsx.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.core.Editor;
import net.mgsx.core.NativeService;
import net.mgsx.core.NativeService.DialogCallback;
import net.mgsx.core.plugins.Movable;
import net.mgsx.core.tools.RectangleTool;

public class AddSpriteTool extends RectangleTool 
{
	private SpriteModel sprite;
	private TextureRegion region;
	
	public AddSpriteTool(Editor editor) {
		super("Sprite", editor);
	}
	
	@Override
	protected void activate() {
		
		// TODO open texture region selector if any registered
		
		// else auto open import window
		
		NativeService.instance.openLoadDialog(new DialogCallback() {
			@Override
			public void selected(FileHandle file) {
				Texture tex = new Texture(file, true);
				tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
				tex.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
				// TODO register texture !
				// TODO store path or something ?
				region = new TextureRegion(tex);
			}
			@Override
			public void cancel() {
			}
		});
	}
	
	@Override
	public void render(Batch batch) 
	{
		if(sprite != null && startPoint != null && endPoint != null){
			Vector2 size = new Vector2(endPoint).sub(startPoint);
			sprite.sprite.setBounds(startPoint.x, startPoint.y, size.x, size.y);
			sprite.sprite.draw(batch);
		}
	}
	
	@Override
	public void render(ShapeRenderer renderer) 
	{
		//if(sprite == null){
			super.render(renderer);
		//}
	}
	
	@Override
	protected void begin(Vector2 startPoint) {
		sprite = new SpriteModel();
		sprite.sprite = new Sprite(region);
		sprite.sprite.setBounds(0, 0, 0, 0);
		sprite.sprite.setOrigin(0,0);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) {
		
		if(sprite != null){
			Entity entity = editor.currentEntity();
			entity.add(sprite);
			entity.add(new Movable(new SpriteMove(sprite.sprite)));
			sprite = null;
		}
	}
	

	
	
}
