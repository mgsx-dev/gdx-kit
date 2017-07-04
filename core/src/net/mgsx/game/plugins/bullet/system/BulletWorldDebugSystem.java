package net.mgsx.game.plugins.bullet.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;

@EditableSystem(isDebug=true)
public class BulletWorldDebugSystem extends EntitySystem
{
	@Inject
	public BulletWorldSystem bulletWorldSystem;

	private DebugDrawer debugDrawer;
		
	private EditorScreen editor;
	
    @Editable public boolean DBG_DrawWireframe = true;
    @Editable public boolean DBG_DrawAabb = true;
    @Editable public boolean DBG_DrawFeaturesText = false;
    @Editable public boolean DBG_DrawContactPoints = false;
    @Editable public boolean DBG_NoDeactivation = false;
    @Editable public boolean DBG_NoHelpText = false;
    @Editable public boolean DBG_DrawText = false;
    @Editable public boolean DBG_ProfileTimings = false;
    @Editable public boolean DBG_EnableSatComparison = false;
    @Editable public boolean DBG_DisableBulletLCP = false;
    @Editable public boolean DBG_EnableCCD = false;
    @Editable public boolean DBG_DrawConstraints = false;
    @Editable public boolean DBG_DrawConstraintLimits = false;
    @Editable public boolean DBG_FastWireframe = false;
    @Editable public boolean DBG_DrawNormals = false;
    @Editable public boolean DBG_DrawFrames = false;
	
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
		int mode = 0;
		
	    if(DBG_DrawWireframe) mode |= DebugDrawModes.DBG_DrawWireframe;
	    if(DBG_DrawAabb) mode |= DebugDrawModes.DBG_DrawAabb;
	    if(DBG_DrawFeaturesText) mode |= DebugDrawModes.DBG_DrawFeaturesText;
	    if(DBG_DrawContactPoints) mode |= DebugDrawModes.DBG_DrawContactPoints;
	    if(DBG_NoDeactivation) mode |= DebugDrawModes.DBG_NoDeactivation;
	    if(DBG_NoHelpText) mode |= DebugDrawModes.DBG_NoHelpText;
	    if(DBG_DrawText) mode |= DebugDrawModes.DBG_DrawText;
	    if(DBG_ProfileTimings) mode |= DebugDrawModes.DBG_ProfileTimings;
	    if(DBG_EnableSatComparison) mode |= DebugDrawModes.DBG_EnableSatComparison;
	    if(DBG_DisableBulletLCP) mode |= DebugDrawModes.DBG_DisableBulletLCP;
	    if(DBG_EnableCCD) mode |= DebugDrawModes.DBG_EnableCCD;
	    if(DBG_DrawConstraints) mode |= DebugDrawModes.DBG_DrawConstraints;
	    if(DBG_DrawConstraintLimits) mode |= DebugDrawModes.DBG_DrawConstraintLimits;
	    if(DBG_FastWireframe) mode |= DebugDrawModes.DBG_FastWireframe;
	    if(DBG_DrawNormals) mode |= DebugDrawModes.DBG_DrawNormals;
	    if(DBG_DrawFrames) mode |= DebugDrawModes.DBG_DrawFrames;
		
		debugDrawer.setDebugMode(mode);

		debugDrawer.begin(editor.game.camera);
		bulletWorldSystem.collisionWorld.debugDrawWorld();
		debugDrawer.flushLines();
		debugDrawer.end();
	}
}
