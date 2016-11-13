package net.mgsx.game.plugins.g2d.tools;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.helpers.AssetLookupCallback;
import net.mgsx.game.core.tools.RectangleTool;
import net.mgsx.game.plugins.g2d.components.SpriteModel;

public class AddSpriteTool extends RectangleTool 
{
	private Sprite sprite;
	private TextureRegion region;
	
	public AddSpriteTool(EditorScreen editor) {
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
			sprite.setBounds(startPoint.x, startPoint.y, size.x, size.y);
			sprite.draw(batch);
		}
	}
	
	@Override
	protected void begin(Vector2 startPoint) {
		sprite = new Sprite(region);
		sprite.setBounds(0, 0, 0, 0);
		sprite.setOrigin(0,0);
	}

	@Override
	protected void create(Vector2 startPoint, Vector2 endPoint) {
		
		if(sprite != null){
			Entity entity = editor.currentEntity();
			SpriteModel component = new SpriteModel();
			component.sprite = new Sprite(sprite);
			entity.add(component);
			sprite = null;
		}
	}
	

	
	
}
