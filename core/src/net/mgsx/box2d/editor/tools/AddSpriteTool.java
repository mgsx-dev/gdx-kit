package net.mgsx.box2d.editor.tools;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.box2d.editor.BodyItem;
import net.mgsx.box2d.editor.SpriteItem;
import net.mgsx.box2d.editor.WorldItem;
import net.mgsx.fwk.editor.NativeService;
import net.mgsx.fwk.editor.NativeService.DialogCallback;
import net.mgsx.fwk.editor.tools.RectangleTool;

public class AddSpriteTool extends RectangleTool 
{
	private WorldItem worldItem;
	private SpriteItem spriteItem;
	
	public AddSpriteTool(Camera camera, WorldItem worldItem) {
		super("Sprite", camera);
		this.worldItem = worldItem;
	}
	
	@Override
	protected void activate() {
		NativeService.instance.openLoadDialog(new DialogCallback() {
			@Override
			public void selected(FileHandle file) {
				Texture tex = new Texture(file, true);
				tex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
				tex.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
				spriteItem = new SpriteItem("sprite", file.path(), new Sprite(tex));
				spriteItem.sprite.setBounds(0, 0, 0, 0);
				spriteItem.sprite.setOrigin(0,0);
			}
			@Override
			public void cancel() {
			}
		});
	}
	
	@Override
	public void render(Batch batch) 
	{
		if(spriteItem != null && startPoint != null && endPoint != null){
			Vector2 size = new Vector2(endPoint).sub(startPoint);
			spriteItem.sprite.setBounds(startPoint.x, startPoint.y, size.x, size.y);
			spriteItem.sprite.draw(batch);
		}
	}
	
	@Override
	public void render(ShapeRenderer renderer) 
	{
		if(spriteItem == null){
			super.render(renderer);
		}
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) {
		
		if(spriteItem != null){
			worldItem.sprites.add(spriteItem);
			BodyItem bodyItem = worldItem.selection.bodies.first();
			bodyItem.sprite = spriteItem;
			spriteItem = null;
		}
	}
	

	
	
}
