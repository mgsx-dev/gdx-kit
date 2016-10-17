package net.mgsx.plugins.sprite;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.core.AssetLookupCallback;
import net.mgsx.core.Editor;
import net.mgsx.core.components.Movable;
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
		
		editor.assetLookup(Texture.class, new AssetLookupCallback<Texture>(){
			@Override
			public void selected(Texture asset) {
				region = new TextureRegion(asset);
			}});
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
