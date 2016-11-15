package net.mgsx.game.plugins.particle2d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

public class P2DRenderSystem extends IteratingSystem {
	private final GameScreen engine;
	private SpriteBatch batch;

	public P2DRenderSystem(GameScreen engine) {
		super(Family.all(Particle2DComponent.class).get(), GamePipeline.RENDER_TRANSPARENT);
		this.engine = engine;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		batch = new SpriteBatch();
	}

	@Override
	public void removedFromEngine(Engine engine) {
		batch.dispose();
		batch = null;
		super.removedFromEngine(engine);
	}

	@Override
	public void update(float deltaTime) 
	{
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		batch.setProjectionMatrix(engine.getRenderCamera().combined);
		batch.begin();
		super.update(deltaTime);
		batch.end();
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Particle2DComponent p = Particle2DComponent.components.get(entity);
		if(p.effect != null){
		    p.effect.draw(batch);
		}
	}
}