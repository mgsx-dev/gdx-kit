package net.mgsx.game.plugins.g3d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.components.Hidden;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

public class G3DTransformSystem extends IteratingSystem {
	public G3DTransformSystem() {
		super(Family.all(G3DModel.class, Transform2DComponent.class).exclude(Hidden.class).get(), GamePipeline.BEFORE_RENDER);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		G3DModel model = entity.getComponent(G3DModel.class);
		Transform2DComponent transformation = entity.getComponent(Transform2DComponent.class);
		if(transformation.enabled){
			model.modelInstance.transform.idt();
			model.modelInstance.transform.translate(transformation.position.x, transformation.position.y, 0); // 0 is sprite plan
			model.modelInstance.transform.rotate(0, 0, 1, transformation.angle * MathUtils.radiansToDegrees);
			model.modelInstance.transform.translate(-model.origin.x, -model.origin.y, -model.origin.z);
			model.modelInstance.transform.translate(-transformation.origin.x, -transformation.origin.y, 0);
		}
	}
}