package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem(isDebug=true)
public class OpenWorldDebugSystem extends EntitySystem
{
	@Inject public DebugRenderSystem debug;
	@Inject public OpenWorldManagerSystem openWorldManager;
	@Editable public float scale = 1000;
	
	public OpenWorldDebugSystem() {
		super(GamePipeline.RENDER_DEBUG);
	}
	
	@Override
	public void update(float deltaTime) {
		debug.shapeRenderer.setColor(Color.GREEN);
		debug.shapeRenderer.begin(ShapeType.Line);
		debug.shapeRenderer.line(
				openWorldManager.viewPoint.x,
				- scale,
				openWorldManager.viewPoint.y,
				openWorldManager.viewPoint.x,
				scale,
				openWorldManager.viewPoint.y);
		debug.shapeRenderer.end();
	}
}
