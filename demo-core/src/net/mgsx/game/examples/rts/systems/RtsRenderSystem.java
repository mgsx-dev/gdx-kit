package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.examples.rts.components.BulletComponent;

@EditableSystem
public class RtsRenderSystem extends IteratingSystem
{
	@Editable
	public float blurSize = 1f;
	
	@Editable
	public Color color = new Color(Color.WHITE);
	
	@Editable
	public float fade = 1e-3f;
	
	private ShapeRenderer renderer;
	private SpriteBatch batch;
	private GameScreen game;
	
	private FrameBuffer fboFront, fboBack;
	private ShaderProgram blurXProgram;
	
	public RtsRenderSystem(GameScreen game) {
		super(Family.all(BulletComponent.class).get(), GamePipeline.RENDER);
		renderer = new ShapeRenderer();
		batch = new SpriteBatch();
		this.game = game;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		fboFront = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		fboBack = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		blurXProgram = new ShaderProgram(
				Gdx.files.internal("shaders/blur-color-vertex.glsl"),
				Gdx.files.internal("shaders/blur-color-fragment.glsl"));
	}
	
	@Override
	public void update(float deltaTime) 
	{
		boolean blur = true;
		if(blur){
			fboFront.bind();
			batch.setShader(blurXProgram);
			batch.begin();
			float rfade = MathUtils.random();
			rfade = 0; //(float)Math.pow(rfade, 50);
			
			blurXProgram.setUniformf("fade", MathUtils.lerp(1 - fade, 0.01f, rfade));
			blurXProgram.setUniformf("dir", new Vector2(blurSize / (float)Gdx.graphics.getWidth(),0));
			batch.draw(fboBack.getColorBufferTexture(), 0, 0);
			batch.end();
			
			fboBack.bind();
			batch.setShader(blurXProgram);
			batch.begin();
			blurXProgram.setUniformf("dir", new Vector2(0, blurSize / (float)Gdx.graphics.getHeight()));
			batch.draw(fboFront.getColorBufferTexture(), 0, 0);
			batch.end();
			
			
		}
		renderer.setColor(color);
		renderer.setProjectionMatrix(game.camera.combined);
		renderer.begin(ShapeType.Filled);
		super.update(deltaTime);
		renderer.end();
		if(blur){
			Gdx.gl.glEnable(GL20.GL_BLEND);
			FrameBuffer.unbind();
			batch.setShader(null);
			batch.begin();
			batch.draw(fboBack.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
			batch.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		float s = .13f;
		BulletComponent bullet = BulletComponent.components.get(entity);
		renderer.setColor(bullet.color);
		renderer.rect(bullet.position.x, bullet.position.y, s, s);
	}

}
