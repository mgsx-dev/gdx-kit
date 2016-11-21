package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;

@EditableSystem
public class Box2DRenderDebugSystem extends EntitySystem 
{
	private final EditorScreen editor;
	private Box2DWorldContext context;
	
	@Editable
	public Box2DDebugRenderer box2dRenderer = new Box2DDebugRenderer();

	public Box2DRenderDebugSystem(EditorScreen editor) {
		super(GamePipeline.RENDER_OVER);
		this.editor = editor;
	}
	
	@Override
	public void addedToEngine(Engine engine) 
	{
		super.addedToEngine(engine);
		context = getEngine().getSystem(Box2DWorldSystem.class).getWorldContext();
	}

	@Override
	public void update(float deltaTime) {
		box2dRenderer.render(context.world, editor.getRenderCamera().combined);
	}
}