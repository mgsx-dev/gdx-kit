package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.helpers.ShaderHelper;
import net.mgsx.game.examples.rts.components.RoadComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem
public class RoadRenderSystem extends IteratingSystem
{
	private GameScreen game;
	private ShaderProgram shader;
	private SpriteBatch batch;
	private Sprite sprite;
	
	@Editable
	public void updateShader(){
		try{
		ShaderProgram shader = new ShaderProgram(
				Gdx.files.internal("shaders/road-vertex.glsl"),
				Gdx.files.internal("shaders/road-fragment.glsl"));
			ShaderHelper.assertProgram(shader);
			this.shader.dispose();
			this.shader = shader;
		}catch(GdxRuntimeException e){
			e.printStackTrace();
		}
	}
	
	public RoadRenderSystem(GameScreen game) {
		super(Family.all(RoadComponent.class).get(), GamePipeline.RENDER);
		this.game = game;
		shader = new ShaderProgram(
				Gdx.files.internal("shaders/road-vertex.glsl"),
				Gdx.files.internal("shaders/road-fragment.glsl"));
		ShaderHelper.assertProgram(shader);
		batch = new SpriteBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal("perlin.png")));
		
		
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(game.camera.combined);
		batch.setShader(shader);
		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		batch.begin();
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		RoadComponent road = entity.getComponent(RoadComponent.class);
		Transform2DComponent src = Transform2DComponent.components.get(road.srcPlanet);
		Transform2DComponent dst = Transform2DComponent.components.get(road.dstPlanet);
		float size = .3f;
		// batch.setColor(planet.color.set(planet.health, planet.solarDistance / 5f, planet.size, planet.population / planet.maxPopulation));
		sprite.setColor(batch.getColor());
		sprite.setOrigin(0, -size / 2);
		sprite.setPosition(src.position.x, src.position.y);
		sprite.setRotation(-90 + dst.position.cpy().sub(src.position).angle());
		sprite.setSize(size, src.position.dst(dst.position));
		sprite.draw(batch);
	}
	
}