package net.mgsx.game.examples.platformer.animations;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.helpers.systems.TransactionSystem;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.camera.components.CullingComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@EditableSystem
public class TubeWorldSystem extends TransactionSystem
{
	private ImmutableArray<Entity> models;
	private ImmutableArray<Entity> cameras;
	
	private Vector3 translation = new Vector3();
	private Quaternion rotation = new Quaternion();
	
	@Editable public float radius = 100;
	@Editable public float offset = 0;
	
	private Camera backup = new PerspectiveCamera();
	
	public TubeWorldSystem() {
		super(GamePipeline.BEFORE_RENDER + 1, GamePipeline.AFTER_RENDER + 1);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		models = engine.getEntitiesFor(Family.all(G3DModel.class).get());
		cameras = engine.getEntitiesFor(Family.all(CameraComponent.class, CullingComponent.class).get());
	}
	
	private void project(Vector3 position, Quaternion direction){
		float r = position.z - radius;
		float t = offset - position.x / (radius * MathUtils.PI2);
		position.set(MathUtils.sin(t * MathUtils.PI2) * r, position.y, MathUtils.cos(t * MathUtils.PI2) * r + radius);
		direction.setFromAxisRad(Vector3.Y, t * MathUtils.PI2);
	}
	
	@Override
	protected void updateBefore(float deltaTime) {
		for(Entity entity : models){
			G3DModel model = G3DModel.components.get(entity);
			
			model.modelInstance.transform.getTranslation(translation);
			rotation.set(Vector3.Z, 0);
			project(translation, rotation);
			
			model.modelInstance.transform.setTranslation(translation);
			model.modelInstance.transform.rotate(rotation);
		}
		
		CameraComponent camera = CameraComponent.components.get(cameras.first());
		
		backup.combined.set(camera.camera.combined);
		backup.position.set(camera.camera.position);
		backup.direction.set(camera.camera.direction);
		rotation.set(Vector3.Z, 0);
		project(camera.camera.position, rotation);

		rotation.transform(camera.camera.direction);
		camera.camera.update();
	}

	@Override
	protected void updateAfter(float deltaTime) {
		CameraComponent camera = CameraComponent.components.get(cameras.first());
		camera.camera.position.set(backup.position);
		camera.camera.direction.set(backup.direction);
		camera.camera.combined.set(backup.combined);
	}

}
