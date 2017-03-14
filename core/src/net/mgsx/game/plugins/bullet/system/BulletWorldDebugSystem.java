package net.mgsx.game.plugins.bullet.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;

@EditableSystem(isDebug=true)
public class BulletWorldDebugSystem extends EntitySystem
{
	@Inject
	public BulletWorldSystem bulletWorldSystem;

	private DebugDrawer debugDrawer;
		
	private EditorScreen editor;
	
	public BulletWorldDebugSystem(EditorScreen editor) {
		super(GamePipeline.RENDER_OVER);
		this.editor = editor;
	}

	@Override
	public void update(float deltaTime)
	{
		if (debugDrawer == null) {
			bulletWorldSystem.collisionWorld.setDebugDrawer(debugDrawer = new DebugDrawer());
		}
		debugDrawer.setDebugMode(DebugDrawModes.DBG_DrawWireframe | DebugDrawModes.DBG_DrawAabb); // DebugDrawModes.DBG_DrawWireframe | DebugDrawModes.DBG_DrawFeaturesText | DebugDrawModes.DBG_DrawText | DebugDrawModes.DBG_DrawContactPoints);
		// debugDrawer.setShapeRenderer(getEngine().getSystem(DebugRenderSystem.class).shapeRenderer);
		debugDrawer.begin(editor.game.camera);
		bulletWorldSystem.collisionWorld.debugDrawWorld();
		debugDrawer.flushLines();
		debugDrawer.end();
	}
}
