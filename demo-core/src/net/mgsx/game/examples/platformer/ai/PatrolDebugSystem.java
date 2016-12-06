package net.mgsx.game.examples.platformer.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.platformer.animations.Character2D;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;

public class PatrolDebugSystem extends IteratingSystem
{
	final private EditorScreen editor;
	private Vector2 start = new Vector2();
	private Vector2 direction = new Vector2();
	private Vector2 end = new Vector2();
	
	public PatrolDebugSystem(EditorScreen editor) {
		super(Family.all(PatrolComponent.class, Box2DBodyModel.class, Character2D.class).get(), GamePipeline.RENDER_DEBUG);
		this.editor = editor;
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
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		PatrolComponent patrol = PatrolComponent.components.get(entity);
		Character2D character = Character2D.components.get(entity);
		
		// check walls
		if(patrol.timeout <= 0){
			float xDir = character.rightToLeft ? -1 : 1;
			start.set(physics.body.getPosition()).add(patrol.rayStart);
			end.set(start).mulAdd(direction.set(xDir, 0), patrol.horizon);
			editor.shapeRenderer.line(start, end);
			if(patrol.checkVoid){
				end.set(start).mulAdd(direction.set(xDir, -1).nor(), patrol.horizon);
				editor.shapeRenderer.line(start, end);
			}
		}
	}

}
