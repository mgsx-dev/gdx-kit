package net.mgsx.game.examples.quantum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import net.mgsx.game.core.EditorScreen;
import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.quantum.components.Planet;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class PlanetRenderer extends IteratingSystem
{
	private ShapeRenderer renderer;
	EditorScreen editor;
	public PlanetRenderer(EditorScreen editor) {
		super(Family.all(Planet.class, Transform2DComponent.class).get(), GamePipeline.RENDER);
		this.editor = editor;
		this.renderer = new ShapeRenderer();
	}
	
	@Override
	public void update(float deltaTime) {
		renderer.setProjectionMatrix(editor.game.camera.combined);
		renderer.begin(ShapeType.Line);
		super.update(deltaTime);
		renderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Planet planet = Planet.components.get(entity);
		renderer.setColor(planet.is_start_planet ? Color.YELLOW : Color.BROWN);
		renderer.circle(transform.position.x, transform.position.y, planet.radius);
	}
}
