package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;

public class MapRenderer extends EntitySystem
{

	protected ShapeRenderer renderer;
	private GameScreen game;
	
	public MapRenderer(GameScreen game) {
		super(GamePipeline.RENDER);
		this.game = game;
		renderer = new ShapeRenderer();
	}
	
	@Override
	public void update(float deltaTime) 
	{
		MapSystem map = getEngine().getSystem(MapSystem.class);
		renderer.setProjectionMatrix(game.camera.combined);
		renderer.begin(ShapeType.Line);
		renderer.setColor(Color.BLUE);
		renderer.rect(0, 0, map.width, map.height);
		renderer.end();
	}
}
