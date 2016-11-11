package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.Editor;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.Movable;
import net.mgsx.game.core.components.Transform2DComponent;
import net.mgsx.game.core.tools.Tool;

public class SelectionRenderSystem extends IteratingSystem {
	final private Editor editor;
	final private Vector3 pos = new Vector3();
	
	public SelectionRenderSystem(Editor editor) {
		super(Family.one(Movable.class, Transform2DComponent.class).get(), GamePipeline.RENDER_OVER);
		this.editor = editor;
	}

	@Override
	public void update(float deltaTime) {
		
		
		editor.shapeRenderer.setProjectionMatrix(editor.camera.combined);
		editor.shapeRenderer.begin(ShapeType.Line);
		
		super.update(deltaTime);
		
		editor.shapeRenderer.end();
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		// TODO optimize with a "selected" component ! or editorComponent with selection flag ?
		Movable movable = Movable.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(movable != null) movable.getPosition(entity, pos);
		else pos.set(transform.position.x, transform.position.y, 0);
		
		Vector2 s = Tool.pixelSize(editor.camera).scl(5);
		boolean inSelection = editor.selection.contains(entity, true);
		if(inSelection) editor.shapeRenderer.setColor(1, 1, 0, 1);
		editor.shapeRenderer.rect(pos.x-s.x, pos.y-s.y, 2*s.x, 2*s.y);
		if(inSelection) editor.shapeRenderer.setColor(1, 1, 1, 1);
	}
}