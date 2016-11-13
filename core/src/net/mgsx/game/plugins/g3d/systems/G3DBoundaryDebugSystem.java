package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.helpers.RenderDebugHelper;
import net.mgsx.game.plugins.g3d.components.G3DModel;
import net.mgsx.game.plugins.g3d.components.NodeBoundary;

@EditableSystem
public class G3DBoundaryDebugSystem extends IteratingSystem 
{
	private final EditorScreen editor;
	
	@Editable
	public boolean boudaryBox = false;
	
	@Editable
	public boolean nodeBoudaryBox = false;
	
	@Editable
	public boolean cameraFrustum = true;
	
	public G3DBoundaryDebugSystem(EditorScreen editor) 
	{
		super(Family.all(G3DModel.class).get(), GamePipeline.RENDER_OVER);
		this.editor = editor;
	}

	@Override
	public void update(float deltaTime) {
		// TODO mode fill switchable : Gdx.gl.glEnable(GL20.GL_BLEND); and editor.shapeRenderer.begin(ShapeType.Filled);
		editor.shapeRenderer.setColor(1, 1, 1, 0.1f);
		editor.shapeRenderer.setProjectionMatrix(editor.camera.combined);
		editor.shapeRenderer.begin(ShapeType.Line);
		
		super.update(deltaTime);
		
		if(cameraFrustum)
		{
			editor.gameCamera.update(true);
			editor.shapeRenderer.setColor(0, 0, 1, 1f);
			RenderDebugHelper.frustum(editor.shapeRenderer, editor.gameCamera.frustum);
			
		}
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