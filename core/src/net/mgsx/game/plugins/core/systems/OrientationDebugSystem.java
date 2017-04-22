package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.core.components.Orientation3D;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem(isDebug=true)
public class OrientationDebugSystem extends IteratingSystem
{
	@Inject
	public DebugRenderSystem render;
	
	private Vector3 a = new Vector3(), b = new Vector3();

	@Editable
	public float scale = 1;
	
	public OrientationDebugSystem() {
		super(Family.all(Transform2DComponent.class, Orientation3D.class).get(), GamePipeline.RENDER_DEBUG);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Orientation3D orientation = Orientation3D.components.get(entity);
		
		a.set(transform.position.x, transform.position.y, transform.depth);
		
		render.shapeRenderer.rotate(1, 0, 0, -90);
		
		render.shapeRenderer.begin(ShapeType.Line);
		
		render.shapeRenderer.setColor(Color.RED);
		b.set(a).mulAdd(orientation.direction, scale);
		render.shapeRenderer.line(a, b);
		
		render.shapeRenderer.setColor(Color.BLUE);
		b.set(a).mulAdd(orientation.normal, scale);
		render.shapeRenderer.line(a, b);
		
		render.shapeRenderer.setColor(Color.GREEN);
		b.set(orientation.direction).crs(orientation.normal).scl(scale).add(a);
		render.shapeRenderer.line(a, b);
		
		render.shapeRenderer.end();
		
		render.shapeRenderer.rotate(1, 0, 0, 90);
	}

}
