package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.helpers.RenderDebugHelper;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.components.NodeBoundary;

@EditableSystem(isDebug=true)
public class G3DBoundaryDebugSystem extends IteratingSystem 
{
	@Inject protected DebugRenderSystem editor;
	
	@Editable
	public boolean boudaryBox = false;
	
	@Editable
	public boolean nodeBoudaryBox = false;
	
	public G3DBoundaryDebugSystem() 
	{
		super(Family.all(G3DModel.class).get(), GamePipeline.RENDER_OVER);
	}

	@Override
	public void update(float deltaTime) {
		// TODO mode fill switchable : Gdx.gl.glEnable(GL20.GL_BLEND); and editor.shapeRenderer.begin(ShapeType.Filled);
		editor.shapeRenderer.setColor(1, 1, 1, 0.1f);
		editor.shapeRenderer.begin(ShapeType.Line);
		
		super.update(deltaTime);
		
		editor.shapeRenderer.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		G3DModel model = G3DModel.components.get(entity);
		editor.shapeRenderer.setColor(1, 0f, 0, 1f);
		BoundingBox box = model.globalBoundary;
		if(boudaryBox)
		{
			RenderDebugHelper.box(editor.shapeRenderer, box);
		}
	
		if(model.inFrustum && nodeBoudaryBox)
			for(NodeBoundary nb : model.boundary)
			{
				editor.shapeRenderer.setColor(1, 0.5f, 0, 1f);
				box = nb.global;
				RenderDebugHelper.box(editor.shapeRenderer, box);
			}
	}
}