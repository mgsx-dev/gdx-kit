package net.mgsx.game.plugins.spline.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;
import net.mgsx.game.plugins.spline.components.PathComponent;
import net.mgsx.game.plugins.spline.components.SplineDebugComponent;

@EditableSystem(isDebug=true)
public class SplineDebugRender extends IteratingSystem {
	@Inject protected DebugRenderSystem editor;

	public SplineDebugRender() {
		super(Family.all(PathComponent.class, SplineDebugComponent.class).get(), GamePipeline.RENDER_DEBUG);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		
		SplineDebugComponent debug = SplineDebugComponent.components.get(entity);
		PathComponent path = PathComponent.components.get(entity);
		
		if(debug.vertices == null){
			
			int dotsPerSegment = 100;
			
			debug.vertices = new Vector3[dotsPerSegment];
			
			for(int i=0 ; i<debug.vertices.length ; i++) debug.vertices[i] = path.path.valueAt(new Vector3(), (float)i / (float)(debug.vertices.length-1));
		}

		
		editor.shapeRenderer.begin(ShapeType.Line);
		
		for(int i=1 ; i<debug.vertices.length ; i++)
			editor.shapeRenderer.line(debug.vertices[i-1], debug.vertices[i]);

		
		editor.shapeRenderer.end();
	}
}