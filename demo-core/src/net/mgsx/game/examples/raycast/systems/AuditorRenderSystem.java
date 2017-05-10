package net.mgsx.game.examples.raycast.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem
public class AuditorRenderSystem extends EntitySystem
{
	@Inject
	public CompassLocalSystem compass;
	@Inject
	public DebugRenderSystem render;
	
	@Editable
	public float r = 1;
	
	public AuditorRenderSystem() {
		super(GamePipeline.RENDER_DEBUG);
	}
	
	@Override
	public void update(float deltaTime) {
		render.shapeRenderer.begin(ShapeType.Line);
		
		float dx = MathUtils.cos(compass.azymuth);
		float dy = MathUtils.sin(compass.azymuth);
		
		render.shapeRenderer.line(compass.x, compass.y, compass.x + dx * r, compass.y + dy * r, Color.RED, Color.BLUE);
		
		render.shapeRenderer.end();
	}
}
