package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class PlanetInfoRenderSystem extends IteratingSystem
{
	private SpriteBatch batch;
	private GameScreen game;
	private BitmapFont font;
	private float fontScale = .1f;
	public PlanetInfoRenderSystem(GameScreen game) {
		super(Family.all(PlanetComponent.class, Transform2DComponent.class).get(), GamePipeline.RENDER);
		batch = new SpriteBatch();
		this.game = game;
		font = new BitmapFont();
		font.setUseIntegerPositions(false);
	}
	
	@Override
	public void update(float deltaTime) {
		Matrix4 mat = new Matrix4();
		mat.scl(fontScale);
		mat.mulLeft( game.camera.combined);
		batch.setProjectionMatrix(mat);
		batch.begin();
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PlanetComponent planet = entity.getComponent(PlanetComponent.class);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		font.draw(batch, String.valueOf((int)planet.population), transform.position.x / fontScale, transform.position.y / fontScale, planet.size * 2 * 1e-4f, Align.center, false);
	}
}
