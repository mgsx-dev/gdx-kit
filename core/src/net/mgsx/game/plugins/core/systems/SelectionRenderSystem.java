package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.tools.Tool;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.editor.systems.DebugRenderSystem;
import net.mgsx.game.plugins.editor.systems.SelectionSystem;

@EditableSystem(isDebug=true)
public class SelectionRenderSystem extends IteratingSystem {
	final private EditorScreen editor;
	@Inject protected DebugRenderSystem render;
	final private Vector3 pos = new Vector3();
	
	@Inject
	private SelectionSystem selection;
	
	public SelectionRenderSystem(EditorScreen editor) {
		super(Family.one(Movable.class, Transform2DComponent.class).get(), GamePipeline.RENDER_TOOLS);
		this.editor = editor;
	}
	
	@Override
	public void update(float deltaTime) {
		
		
		render.shapeRenderer.setProjectionMatrix(editor.getGameCamera().combined);
		render.shapeRenderer.begin(ShapeType.Line);
		
		super.update(deltaTime);
		
		render.shapeRenderer.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		// TODO optimize with a "selected" component ! or editorComponent with selection flag ?
		Movable movable = Movable.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(movable != null) movable.getPosition(entity, pos);
		else pos.set(transform.position.x, transform.position.y, 0);
		
		Vector2 s = Tool.pixelSize(editor.getGameCamera()).scl(5);
		boolean inSelection = selection.contains(entity);
		if(inSelection) render.shapeRenderer.setColor(1, 1, 0, 1);
		else render.shapeRenderer.setColor(Color.WHITE);
		render.shapeRenderer.rect(pos.x-s.x, pos.y-s.y, 2*s.x, 2*s.y);
		if(inSelection) render.shapeRenderer.setColor(1, 1, 1, 1);
	}
}