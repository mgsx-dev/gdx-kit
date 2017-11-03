package net.mgsx.game.plugins.g2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.g2d.components.BehindComponent;
import net.mgsx.game.plugins.g2d.components.SpriteModel;

@Storable("g2d.render")
@EditableSystem("G2D Render")
public class G2DRenderSystem extends IteratingSystem {
	private final GameScreen engine;
	private SpriteBatch batch = new SpriteBatch();

	@Editable public boolean culling = false;
	@Editable public boolean depthTest = false;
	
	public G2DRenderSystem(GameScreen engine) {
		super(Family.all(SpriteModel.class).exclude(Hidden.class, BehindComponent.class).get(), GamePipeline.RENDER + 1); // TODO transparent ?
		this.engine = engine;
	}

	@Override
	public void update(float deltaTime) {
		batch.setProjectionMatrix(engine.camera.combined);
		batch.begin();
		if(depthTest){
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		}else{
			Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		}
		super.update(deltaTime);
		batch.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		// optional component
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		if(!culling || boundary == null || boundary.inside)
			SpriteModel.components.get(entity).sprite.draw(batch);
	}
}