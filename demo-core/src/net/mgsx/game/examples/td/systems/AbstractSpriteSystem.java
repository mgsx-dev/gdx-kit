package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.mgsx.game.core.GameScreen;

public abstract class AbstractSpriteSystem extends IteratingSystem
{
	protected SpriteBatch batch;
	protected GameScreen game;
	protected Sprite sprite;
	
	public AbstractSpriteSystem(GameScreen game, Family family, int priority) {
		super(family, priority);
		this.game = game;
		batch = new SpriteBatch();
		sprite = new Sprite();
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(game.camera.combined);
		batch.begin();
		super.update(deltaTime);
		batch.end();
	}
	
}
