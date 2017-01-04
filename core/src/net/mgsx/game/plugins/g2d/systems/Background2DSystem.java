package net.mgsx.game.plugins.g2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.plugins.g2d.components.BehindComponent;
import net.mgsx.game.plugins.g2d.components.SpriteModel;

@EditableSystem
public class Background2DSystem extends IteratingSystem
{
	private final GameScreen game;
	private SpriteBatch batch = new SpriteBatch();
	
	public Background2DSystem(GameScreen game) {
		super(Family.all(SpriteModel.class, BehindComponent.class).exclude(Hidden.class).get(), GamePipeline.AFTER_RENDER_OPAQUE - 1);
		this.game = game;
	}

	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1));
		batch.begin();
		
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST); // required to write in depth buffer
		Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
		Gdx.gl.glDepthRangef(1,1);
		super.update(deltaTime);
		batch.end();
		
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glDepthRangef(0,1);
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		BehindComponent behind = BehindComponent.components.get(entity);
		
		float px = game.camera.position.x * behind.parallax.x + behind.offset.x;
		float py = game.camera.position.y * behind.parallax.y + behind.offset.y;
		
		Texture texture = SpriteModel.components.get(entity).sprite.getTexture();
		
		float texRatio = (float)texture.getWidth() / (float)texture.getHeight();
		float scrRatio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		
		float uRatio = scrRatio / texRatio;
		
		if(texture.getUWrap() != TextureWrap.Repeat || texture.getVWrap() != TextureWrap.Repeat){
			texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat); // TODO when added only or in sprite component ??
		}
		float u = 0, v = 1f, u2 = uRatio, v2 = 0;
		u += px;
		u2 += px;
		v += py;
		v2 += py;
		batch.draw(texture, 0, 0, 1, 1, u, v, u2, v2);
	}
	
}
