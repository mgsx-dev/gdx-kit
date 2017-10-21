package net.mgsx.game.examples.convoy.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.convoy.components.Conveyor;
import net.mgsx.game.examples.convoy.components.Selected;
import net.mgsx.game.examples.convoy.model.MaterialType;
import net.mgsx.game.plugins.camera.model.POVModel;

public class ConveyorRenderSystem extends IteratingSystem
{
	@Inject POVModel pov;
	
	private ShapeRenderer renderer;

	private Matrix4 transform = new Matrix4();

	public ConveyorRenderSystem() {
		super(Family.all(Conveyor.class).get(), GamePipeline.RENDER);
		renderer = new ShapeRenderer();
	}

	@Override
	public void update(float deltaTime) {
		renderer.setProjectionMatrix(pov.camera.combined);
		renderer.begin(ShapeType.Filled);
		super.update(deltaTime);
		renderer.end();
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Conveyor conveyor = Conveyor.components.get(entity);
		float scale = .3f;
		renderer.setTransformMatrix(transform.
				idt()
				.translate(conveyor.position.x, conveyor.position.y, 0)
				.rotate(Vector3.Z, conveyor.angle)
				.scale(scale, scale, scale));
		
		renderer.setColor(Selected.components.has(entity) ? Color.GRAY : Color.BLACK);
		renderer.rect(-1, -1, 10, 2f);
		renderer.circle(0, 0, 2, 12);;
		float totalRate = 0;
		for(MaterialType type : MaterialType.ALL){
			float rate = conveyor.materials.get(type) / conveyor.storageCapacity;
			if(rate <= 0) continue;
			renderer.setColor(type.color);
			renderer.rect(2.5f + 6 * totalRate, -.75f, 6 * rate, 1.5f);
			totalRate += rate;
		}
		renderer.setColor(Color.PURPLE);
		renderer.arc(0, 0, 1.5f, 0, 360 * conveyor.oil / conveyor.oilMax, 12);
		renderer.setColor(Color.RED);
		renderer.arc(0, 0, 1.5f, 0, 360 * Math.min(conveyor.oilRequired, conveyor.oil) / conveyor.oilMax, 12);
		
	}

}
