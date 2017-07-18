package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.OpenWorldCamera;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.game.plugins.camera.components.CameraComponent;

@Storable(value="ow.camera")
@EditableSystem
public class OpenWorldCameraSystem extends EntitySystem
{
	@Inject BulletWorldSystem bulletWorld;
	@Inject OpenWorldManagerSystem openWorldManager;
	@Inject OpenWorldWaterRenderSystem waterRender;
	
	private GameScreen screen;
	
	private ClosestRayResultCallback resultCallback;
	
	@Editable public float speed = 2; 
	@Editable public float offset = 5; 
	@Editable public boolean clipToWater = true; 
	
	private Vector2 dir = new Vector2();
	private Vector2 pos = new Vector2();

	private Ray ray = new Ray();
	@Editable(realtime=true, readonly=true) public float totalMove = 0;

	public OpenWorldCameraSystem(GameScreen screen) {
		super(GamePipeline.INPUT);
		this.screen = screen;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		// resultCallback = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		// resultCallback.release();
		super.removedFromEngine(engine);
	}
	
	@Override
	public void update(float deltaTime) {
		
		ImmutableArray<Entity> cameras = getEngine().getEntitiesFor(Family.all(OpenWorldCamera.class, CameraComponent.class).get());
		if(cameras.size() == 0) return;
		
		Entity entity = cameras.first();
		
		float move = 0;
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			move = 1;
		} else if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
			move = -1;
		}
		totalMove  += Math.abs(move);
		
		CameraComponent camera = CameraComponent.components.get(entity);
		// rotate camera
		if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
			camera.camera.direction.set(Vector3.Z);
			camera.camera.direction.rotate(Vector3.X, ((float)Gdx.input.getY() / (float)Gdx.graphics.getHeight() -.5f) * 360);
			camera.camera.direction.rotate(Vector3.Y, -(float)Gdx.input.getX() / (float)Gdx.graphics.getWidth() * 2 * 360);
		}
		
		// move camera (2D plan)
		dir.set(camera.camera.direction.x, camera.camera.direction.z).nor();
		
		pos.set(camera.camera.position.x, camera.camera.position.z);
		
		pos.mulAdd(dir, move * speed * deltaTime);
		
		camera.camera.position.x = pos.x;
		camera.camera.position.z = pos.y;
		
		camera.camera.position.y = 0;
		
		// TODO ray cast for Y !
		ray.origin.set(camera.camera.position);
		ray.direction.set(0,-1, 0);
		ray.origin.mulAdd(ray.direction, -20f);
		
		
		resultCallback = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
		//resultCallback.setCollisionObject(null);
		bulletWorld.collisionWorld.rayTest(ray.origin, ray.origin.cpy().mulAdd(ray.direction, 100), resultCallback);
		
		if(resultCallback.hasHit()){
			Vector3 p = new Vector3();
			resultCallback.getHitPointWorld(p);
			Vector3 p2 = ray.origin.cpy().mulAdd(ray.direction, 100 * resultCallback.getClosestHitFraction());
			camera.camera.position.y = p2.y + offset;
			if(clipToWater){
				float waterLimit = -openWorldManager.scale * waterRender.level + camera.camera.near * (float)Math.sqrt(2);
				camera.camera.position.y = Math.max(camera.camera.position.y, waterLimit);
			}
		
		}
		resultCallback.release();
		
		// Apply to logic point of view !
		openWorldManager.viewPoint.set(camera.camera.position.x, camera.camera.position.z);
		
		// update
		camera.camera.update();
	}
	
}
