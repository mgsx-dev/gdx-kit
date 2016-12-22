package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;

@EditableSystem
public class Box2DBoundaryDebugSystem extends IteratingSystem 
{
	private final EditorScreen editor;
	
	@Editable
	public Box2DDebugRenderer box2dRenderer = new Box2DDebugRenderer();

	public Box2DBoundaryDebugSystem(EditorScreen editor) {
		super(Family.all(Box2DBodyModel.class).get(), GamePipeline.RENDER_OVER);
		this.editor = editor;
	}
	
	@Override
	public void update(float deltaTime) {
		editor.shapeRenderer.begin(ShapeType.Line);
		editor.shapeRenderer.setColor(Color.PURPLE);
		super.update(deltaTime);
		editor.shapeRenderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		if(physics.bounds.area() > 0){
			Vector2 pos = physics.body.getPosition();
			editor.shapeRenderer.rect(pos.x + physics.bounds.x, pos.y + physics.bounds.y, physics.bounds.width, physics.bounds.height);
		}
	}
}