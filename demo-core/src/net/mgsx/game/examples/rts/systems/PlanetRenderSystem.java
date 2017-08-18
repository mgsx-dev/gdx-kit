package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.helpers.ShaderProgramHelper;
import net.mgsx.game.examples.rts.components.PlanetComponent;
import net.mgsx.game.examples.rts.logic.RtsFactory;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem
public class PlanetRenderSystem extends IteratingSystem
{
	private GameScreen game;
	private ShaderProgram shader;
	private SpriteBatch batch;
	private Sprite sprite;
	
	@Editable
	public void updateShader(){
		shader = ShaderProgramHelper.reload(shader,
				Gdx.files.internal("rts/planet-vertex.glsl"),
				Gdx.files.internal("rts/planet-fragment.glsl"));
	}
	
	public PlanetRenderSystem(GameScreen game) {
		super(Family.all(PlanetComponent.class, Transform2DComponent.class).get(), GamePipeline.RENDER);
		this.game = game;
		updateShader();
		batch = new SpriteBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal("perlin.png")));
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(game.camera.combined);
		batch.setShader(shader);
		batch.enableBlending();
		batch.begin();
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PlanetComponent planet = entity.getComponent(PlanetComponent.class);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		shader.setUniformf("u_color", RtsFactory.colors[planet.owner + 1]);
		batch.setColor(planet.color.set(planet.health, planet.solarDistance / 5f, planet.size, planet.population / planet.maxPopulation));
		sprite.setColor(batch.getColor());
		sprite.setBounds(transform.position.x - planet.size, transform.position.y - planet.size, planet.size * 2, planet.size * 2);
		sprite.draw(batch);
		batch.flush();
	}
}
