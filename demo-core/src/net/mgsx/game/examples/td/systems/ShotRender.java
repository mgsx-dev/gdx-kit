package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Asset;
import net.mgsx.game.examples.td.components.Shot;

public class ShotRender extends AbstractShapeSystem
{
	@Asset("td/bullet.png") // TODO how to deal with atlas ... should be abstracted like in skins!
	public Texture bullet;
	
	private SpriteBatch batch;
	
	private Sprite sprite = new Sprite();
	
	private Vector2 pos = new Vector2();
	
	public ShotRender(GameScreen game) {
		super(game, Family.all(Shot.class).get(), GamePipeline.RENDER);
		batch = new SpriteBatch();
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(game.camera.combined);
		batch.begin();
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Shot shot = Shot.components.get(entity);
		final float s = .1f;
		
		pos.set(shot.start).lerp(shot.end, shot.t);
		
		sprite.setRegion(bullet);
		sprite.setBounds(pos.x - s, pos.y - s, 2*s, 2*s);
		
		sprite.draw(batch);
	}
}
