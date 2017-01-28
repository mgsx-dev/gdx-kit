package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GameScreen;

public abstract class AbstractShapeSystem extends IteratingSystem
{
	protected ShapeRenderer renderer;
	private GameScreen game;
	
	public AbstractShapeSystem(GameScreen game, Family family, int priority) {
		super(family, priority);
		this.game = game;
		renderer = new ShapeRenderer();
	}
	
	@Override
	public void update(float deltaTime) {
		renderer.setProjectionMatrix(game.camera.combined);
		renderer.begin(ShapeType.Filled);
		super.update(deltaTime);
		renderer.end();
	}
	
}
