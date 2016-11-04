package net.mgsx.game.plugins.boundary.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.helpers.RenderDebugHelper;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;

public class BoundaryDebugSystem extends IteratingSystem
{
	private GameEngine engine;
	
	public BoundaryDebugSystem(GameEngine engine) 
	{
		super(Family.one(BoundaryComponent.class).get(), GamePipeline.RENDER_DEBUG);
		this.engine = engine;
	}
	
	@Override
	public void update(float deltaTime) {
		engine.shapeRenderer.begin(ShapeType.Line);
		super.update(deltaTime);
		engine.shapeRenderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		engine.shapeRenderer.setColor(boundary.inside ? Color.RED : Color.GRAY);
		RenderDebugHelper.box(engine.shapeRenderer, boundary.box);
	}

}
