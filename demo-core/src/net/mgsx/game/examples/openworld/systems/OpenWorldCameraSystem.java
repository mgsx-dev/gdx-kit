package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
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
	public static interface CameraMatrixProvider {
		public float getAzymuth();
		public float getPitch();
		public float getRoll();
		public void update();
	}
	public static CameraMatrixProvider cameraMatrixProvider = null;
	
	@Inject BulletWorldSystem bulletWorld;
	@Inject OpenWorldManagerSystem openWorldManager;
	@Inject OpenWorldEnvSystem env;
	
	private GameScreen screen;
	
	private ClosestRayResultCallback resultCallback;
	
	@Editable public float speed = 2; 
	@Editable public float offset = 5; 
	@Editable public boolean clipToWater = true; 
	@Editable public boolean clipToGround = true; 
	@Editable public boolean flyingMode = false; 
	
	@Editable public float smoothing = 10;
	
	private Vector2 dir = new Vector2();
	private Vector2 pos = new Vector2();
	
	private Vector3 focus = new Vector3();
	private Vector3 up = new Vector3();

	private Ray ray = new Ray();
	@Editable(realtime=true, readonly=true) public float totalMove = 0;
	
	private boolean enableControl = true, buttonWasPressed;
	private int prevX, prevY;

	public OpenWorldCameraSystem(GameScreen screen) {
		super(GamePipeline.INPUT);
		this.screen = screen;
	}
	
	@Editable
	public void resetPosition(){
		ImmutableArray<Entity> cameras = getEngine().getEntitiesFor(Family.all(OpenWorldCamera.class, CameraComponent.class).get());
		if(cameras.size() == 0) return;
		
		Entity entity = cameras.first();
		CameraComponent camera = CameraComponent.components.get(entity);
		camera.camera.position.setZero();
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
		CameraComponent camera = CameraComponent.components.get(entity);
		
		if(enableControl){
			
			float moveFront = 0;
			float moveSide = 0;
			float moveTop = 0;
			
			
			if(Gdx.app.getType() == ApplicationType.Desktop)
			{
				if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
					moveFront = 1;
				} else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
					moveFront = -1;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.D)) {
					moveSide = 1;
				} else if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
					moveSide = -1;
				}
				if(Gdx.input.isKeyPressed(Input.Keys.E)) {
					moveTop = 1;
				} else if(Gdx.input.isKeyPressed(Input.Keys.X)) {
					moveTop = -1;
				}
			}
			else
			{
				if(Gdx.input.isTouched()){
					moveSide = 2 * (Gdx.input.getX() / (float)Gdx.graphics.getWidth() - .5f);
					moveFront = -2 * (Gdx.input.getY() / (float)Gdx.graphics.getHeight() - .5f);
				}
			}
			
			// rotate camera
			if(cameraMatrixProvider != null){
				
				cameraMatrixProvider.update();
				
				focus.set(Vector3.Z)
					.rotate(Vector3.X, cameraMatrixProvider.getPitch() * MathUtils.radiansToDegrees)
					.rotate(Vector3.Y, -cameraMatrixProvider.getAzymuth() * MathUtils.radiansToDegrees)
					;
				camera.camera.direction.lerp(focus, MathUtils.clamp(deltaTime * smoothing, 0, 1));
				
				up.set(Vector3.Y).rotate(Vector3.X, cameraMatrixProvider.getRoll() * MathUtils.radiansToDegrees);
				
				camera.camera.up.lerp(up, MathUtils.clamp(deltaTime * smoothing, 0, 1));
			}
			else if(Gdx.app.getType() == ApplicationType.Desktop)
			{
				boolean buttonPressed = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
				if(!buttonWasPressed && buttonPressed){
					buttonWasPressed = true;
					prevX = Gdx.input.getX();
					prevY = Gdx.input.getY();
					
					focus.set(camera.camera.direction);
					
				}else if(!buttonPressed && buttonWasPressed){
					buttonWasPressed = false;
				}
				if(buttonPressed){
					// camera.camera.direction.set(Vector3.Z);
					float elevation = (float)(Gdx.input.getY() - prevY) / (float)Gdx.graphics.getHeight();
					float angle = (float)(Gdx.input.getX() - prevX) / (float)Gdx.graphics.getWidth() * 2 * 360;
					
					// focus.set(camera.camera.direction);
					focus.rotate(Vector3.Y, -angle);
					focus.y -= elevation * 5;
					focus.nor();
					
					camera.camera.direction.lerp(focus, MathUtils.clamp(deltaTime * smoothing, 0, 1));
					
					prevX = Gdx.input.getX();
					prevY = Gdx.input.getY();
				}
			}
			
			// move camera 2D plan (walking)
			if(!flyingMode){
				dir.set(camera.camera.direction.x, camera.camera.direction.z).nor();
				
				pos.set(camera.camera.position.x, camera.camera.position.z);
				
				Vector2 tan = new Vector2(-dir.y, dir.x);
				
				pos.mulAdd(dir, moveFront * speed * deltaTime);
				pos.mulAdd(tan, moveSide * speed * deltaTime);
				
				camera.camera.position.x = pos.x;
				camera.camera.position.z = pos.y;
			}
			// move camera 3D space (flying)
			else{
				
				Vector3 tan3d = camera.camera.direction.cpy().crs(camera.camera.up).nor();
				Vector3 nor3d = tan3d.cpy().crs(camera.camera.direction).nor();
				
				camera.camera.position.mulAdd(camera.camera.direction, moveFront * speed * deltaTime);
				camera.camera.position.mulAdd(tan3d, moveSide * speed * deltaTime);
				camera.camera.position.mulAdd(nor3d, moveTop * speed * deltaTime);
			}
			
			
			float moveLen = (moveFront + moveSide) * speed * deltaTime;
			totalMove += Math.abs(moveLen);
		}
		
		// ray cast for Y
		if(clipToGround){
			
			ray.origin.set(camera.camera.position);
			ray.origin.y = 0;
			ray.direction.set(0,-1, 0);
			ray.origin.mulAdd(ray.direction, -20f);
			
			
			resultCallback = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
			//resultCallback.setCollisionObject(null);
			bulletWorld.collisionWorld.rayTest(ray.origin, ray.origin.cpy().mulAdd(ray.direction, 100), resultCallback);
			
			if(resultCallback.hasHit()){
				Vector3 p = new Vector3();
				resultCallback.getHitPointWorld(p);
				Vector3 p2 = ray.origin.cpy().mulAdd(ray.direction, 100 * resultCallback.getClosestHitFraction());
				float elevation = p2.y + offset;
				
				if(clipToWater){
					float waterLimit = - env.waterLevel + camera.camera.near * (float)Math.sqrt(2);
					elevation = Math.max(elevation, waterLimit);
				}
				if(!flyingMode || elevation > camera.camera.position.y){
					camera.camera.position.y = elevation;
				}
				
			}
			resultCallback.release();
			
		}
		
		// Apply to logic point of view !
		openWorldManager.viewPoint.set(camera.camera.position.x, camera.camera.position.z);
		
		// update
		camera.camera.update();
	}
	
}
