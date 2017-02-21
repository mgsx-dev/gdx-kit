package net.mgsx.game.plugins.editor.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;

public class DebugRenderSystem extends EntitySystem
{
	public final ShapeRenderer shapeRenderer;
	public final SpriteBatch editorBatch;
	private final EditorScreen screen;
	public DebugRenderSystem(EditorScreen screen) {
		super(GamePipeline.RENDER_DEBUG-1);
		editorBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		this.screen = screen;
	}
	
	@Override
	public void update(float deltaTime) 
	{
		shapeRenderer.setProjectionMatrix(screen.getGameCamera().combined);
		editorBatch.setProjectionMatrix(screen.getGameCamera().combined);
		super.update(deltaTime);
	}
}
