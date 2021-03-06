package net.mgsx.game.plugins.boundary.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.helpers.RenderDebugHelper;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;

@EditableSystem(isDebug=true)
public class BoundaryDebugSystem extends IteratingSystem
{
	@Inject protected DebugRenderSystem editor;
	
	public BoundaryDebugSystem(EditorScreen editor) 
	{
		super(Family.one(BoundaryComponent.class).get(), GamePipeline.RENDER_DEBUG);
	}
	
	@Override
	public void update(float deltaTime) {
		editor.shapeRenderer.begin(ShapeType.Line);
		super.update(deltaTime);
		editor.shapeRenderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		editor.shapeRenderer.setColor(boundary.inside ? Color.RED : Color.GRAY);
		RenderDebugHelper.box(editor.shapeRenderer, boundary.box);
	}

}
