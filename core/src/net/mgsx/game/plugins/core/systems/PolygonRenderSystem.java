package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.core.components.PolygonComponent;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem(isDebug=true)
public class PolygonRenderSystem extends IteratingSystem
{
	@Inject protected DebugRenderSystem editor;

	public PolygonRenderSystem() {
		super(Family.all(PolygonComponent.class).get(), GamePipeline.RENDER_DEBUG);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		PolygonComponent polygon = PolygonComponent.components.get(entity);
		
		editor.shapeRenderer.begin(ShapeType.Line);
		
		for(int i=1 ; i<polygon.vertex.size ; i++)
		{
			editor.shapeRenderer.line(polygon.vertex.get(i-1), polygon.vertex.get(i));
		}
		if(polygon.loop) 
		{
			editor.shapeRenderer.line(polygon.vertex.get(polygon.vertex.size-1), polygon.vertex.get(0));
		}
		editor.shapeRenderer.end();
		
	}
}
