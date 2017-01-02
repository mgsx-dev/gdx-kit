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
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.systems.TransactionSystem;
import net.mgsx.game.plugins.camera.components.ActiveCamera;
import net.mgsx.game.plugins.camera.components.CameraComponent;
import net.mgsx.game.plugins.g3d.components.G3DModel;

@Storable("example.platformer.tube")
@EditableSystem
public class TubeWorldSystem extends TransactionSystem
{
	public static enum Mode{
		X,Y,Z, None
	}
	
	private GameScreen game;
	private ImmutableArray<Entity> models, tubes;
	private ImmutableArray<Entity> cameras;
	
	private Vector3 translation = new Vector3();
	private Quaternion rotation = new Quaternion();
	
	@Editable public float radius = 30;
	@Editable public float offset = 0;
	@Editable public float start = 0;
	@Editable public int loops = 1;
	@Editable public boolean limits;
	
	@Editable 
	public Mode mode = Mode.None;
	
	private Camera backup = new PerspectiveCamera();
	
	public TubeWorldSystem(GameScreen game) {
		super(GamePipeline.BEFORE_RENDER + 1, new AfterSystem(GamePipeline.LAST + 1){});
		this.game = game;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		models = engine.getEntitiesFor(Family.all(G3DModel.class).exclude(TubeComponent.class).get());
		tubes = engine.getEntitiesFor(Family.all(G3DModel.class, TubeComponent.class).get());
		cameras = engine.getEntitiesFor(Family.all(CameraComponent.class, ActiveCamera.class).get());
	}
	
	private void project(Vector3 position, Quaternion direction){
		
		if(mode == Mode.Y){
			float r = (position.z - offset) - radius;
			float t = (position.x - start) / (radius * MathUtils.PI2);
			
			if(limits && position.x < start){
				// do nothing
			}else if(limits && Math.abs(t) > loops){
				position.x -= loops * Math.abs(radius * MathUtils.PI2);
			}else{
				position.set(start - (float)Math.sin(t * MathUtils.PI2) * r, position.y, (float)Math.cos(t * MathUtils.PI2) * r + radius);
				direction.setFromAxisRad(new Vector3(0,-1,0), t * MathUtils.PI2);
			}
		}else if(mode == Mode.Z){
			
			float r = position.y - radius;
			float t = offset - position.x / (radius * MathUtils.PI2);
			position.set(MathUtils.sin(t * MathUtils.PI2) * r, MathUtils.cos(t * MathUtils.PI2) * r + radius , position.z );
			direction.setFromAxisRad(new Vector3(0,0,-1), t * MathUtils.PI2);
		}else if(mode == Mode.X){
			float r = position.z - radius;
			float t = offset - position.y / (radius * MathUtils.PI2);
			position.set(position.x, MathUtils.sin(t * MathUtils.PI2) * r+ radius, MathUtils.cos(t * MathUtils.PI2) * r );
			direction.setFromAxisRad(new Vector3(-1,0,0), t * MathUtils.PI2);
			
		}
		
	}
	
	@Override
	protected boolean updateBefore(float deltaTime) {
		
		if(mode == Mode.None){
			return false;
		}
		
		for(Entity entity : models){
			G3DModel model = G3DModel.components.get(entity);
			
			model.modelInstance.transform.getTranslation(translation);
			rotation.set(Vector3.Z, 0);
			project(translation, rotation);
			
			model.modelInstance.transform.setTranslation(translation);
			model.modelInstance.transform.rotate(rotation);
		}
		
		if(cameras.size() == 0) return false;
		
		Camera camera = CameraComponent.components.get(cameras.first()).camera;
		
		backup.combined.set(camera.combined);
		backup.position.set(camera.position);
		backup.direction.set(camera.direction);
		backup.up.set(camera.up);
		
		
//		backup.position.set(camera.position);
//		backup.direction.set(camera.direction);
//		backup.combined.set(camera.combined);
//		backup.invProjectionView.set(camera.invProjectionView);
//		backup.projection.set(camera.projection);
//		backup.up.set(camera.up);
//		backup.view.set(camera.view);
//		backup.viewportHeight = camera.viewportHeight;
//		backup.far = camera.far;
//		backup.near = camera.near;
//		backup.viewportWidth = camera.viewportWidth;
//		backup.frustum.update(camera.invProjectionView);
		
		
		rotation.set(Vector3.Z, 0);
		project(camera.position, rotation);

		rotation.transform(camera.direction);
		rotation.transform(camera.up);
		camera.update();
		
		for(Entity entity : tubes){
			G3DModel model = G3DModel.components.get(entity);
			model.inFrustum = camera.frustum.boundsInFrustum(model.globalBoundary);
		}
		
		
		game.camera.position.set(camera.position);
		game.camera.direction.set(camera.direction);
		game.camera.combined.set(camera.combined);
		game.camera.invProjectionView.set(camera.invProjectionView);
		game.camera.projection.set(camera.projection);
		game.camera.up.set(camera.up);
		game.camera.view.set(camera.view);
		game.camera.viewportHeight = camera.viewportHeight;
		game.camera.far = camera.far;
		game.camera.near = camera.near;
		game.camera.viewportWidth = camera.viewportWidth;
		game.camera.frustum.update(camera.invProjectionView);

		
		return true;
	}

	@Override
	protected void updateAfter(float deltaTime) {
		if(cameras.size() == 0) return;
		Camera camera = CameraComponent.components.get(cameras.first()).camera;
		
		
//		camera.position.set(backup.position);
//		camera.direction.set(backup.direction);
//		camera.combined.set(backup.combined);
//		camera.invProjectionView.set(backup.invProjectionView);
//		camera.projection.set(backup.projection);
//		camera.up.set(backup.up);
//		camera.view.set(backup.view);
//		camera.viewportHeight = camera.viewportHeight;
//		camera.far = camera.far;
//		camera.near = camera.near;
//		camera.viewportWidth = camera.viewportWidth;
//		camera.frustum.update(backup.invProjectionView);
		
		
		camera.position.set(backup.position);
		camera.direction.set(backup.direction);
		camera.up.set(backup.up);
		camera.combined.set(backup.combined);
		//camera.update(true);
		
//		game.camera.position.set(camera.position);
//		game.camera.direction.set(camera.direction);
//		game.camera.combined.set(camera.combined);
//		game.camera.invProjectionView.set(camera.invProjectionView);
//		game.camera.projection.set(camera.projection);
//		game.camera.up.set(camera.up);
//		game.camera.view.set(camera.view);
//		game.camera.viewportHeight = camera.viewportHeight;
//		game.camera.far = camera.far;
//		game.camera.near = camera.near;
//		game.camera.viewportWidth = camera.viewportWidth;
//		game.camera.frustum.update(camera.invProjectionView);


	}

}
