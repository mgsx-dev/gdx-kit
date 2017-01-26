package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
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
import net.mgsx.game.examples.rts.components.SunComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

@EditableSystem
public class SunRenderSystem extends IteratingSystem
{
	private GameScreen game;
	private ShaderProgram shader;
	private SpriteBatch batch;
	private Sprite sprite;
	
	@Editable
	public void updateShader(){
		try{
		ShaderProgram shader = new ShaderProgram(
				Gdx.files.internal("shaders/sun-vertex.glsl"),
				Gdx.files.internal("shaders/sun-fragment.glsl"));
			ShaderHelper.assertProgram(shader);
			this.shader.dispose();
			this.shader = shader;
		}catch(GdxRuntimeException e){
			e.printStackTrace();
		}
	}
	
	public SunRenderSystem(GameScreen game) {
		super(Family.all(SunComponent.class, Transform2DComponent.class).get(), GamePipeline.RENDER);
		this.game = game;
		shader = new ShaderProgram(
				Gdx.files.internal("shaders/sun-vertex.glsl"),
				Gdx.files.internal("shaders/sun-fragment.glsl"));
		ShaderHelper.assertProgram(shader);
		batch = new SpriteBatch();
		sprite = new Sprite(new Texture(Gdx.files.internal("perlin.png")));
		
		
	}
	
	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(game.camera.combined);
		batch.setShader(shader);
		batch.enableBlending();
		batch.begin();
		shader.setUniformf("uu_time", (GdxAI.getTimepiece().getTime() * 0.01f) % 1.f);
		super.update(deltaTime);
		batch.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SunComponent sun = entity.getComponent(SunComponent.class);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		sprite.setColor(sun.color);
		sprite.setBounds(transform.position.x - sun.size, transform.position.y - sun.size, sun.size * 2, sun.size * 2);
		sprite.draw(batch);
	}
}
