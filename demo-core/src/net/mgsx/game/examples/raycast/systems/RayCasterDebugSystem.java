package net.mgsx.game.examples.raycast.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.raycast.components.RayCaster;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem(isDebug=false) // XXX
public class RayCasterDebugSystem extends IteratingSystem
{
	@Inject
	public DebugRenderSystem render;
	
	public RayCasterDebugSystem() {
		super(Family.all(Transform2DComponent.class, RayCaster.class).get(), GamePipeline.RENDER_DEBUG);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		RayCaster rayCaster = RayCaster.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		render.shapeRenderer.begin(ShapeType.Filled);
		render.shapeRenderer.setColor(Color.BLACK);
		int n = rayCaster.range >= 180 ? rayCaster.dots.size : rayCaster.dots.size-1;
		for(int i=0 ; i<n ; i++)
		{
			Vector2 a = rayCaster.dots.get(i);
			Vector2 b = rayCaster.dots.get((i+1)%rayCaster.dots.size);
			render.shapeRenderer.triangle(
					transform.position.x, transform.position.y, 
					a.x, a.y, b.x, b.y);
			
		}

		render.shapeRenderer.end();
		
	}
}
