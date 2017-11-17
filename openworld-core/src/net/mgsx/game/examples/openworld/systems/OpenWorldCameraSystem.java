package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.OpenWorldCamera;
import net.mgsx.game.examples.openworld.utils.BulletBuilder;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;
import net.mgsx.game.plugins.camera.components.ActiveCamera;
import net.mgsx.game.plugins.camera.components.CameraComponent;

@Storable(value="ow.camera")
@EditableSystem
public class OpenWorldCameraSystem extends EntitySystem
{
	public static int ROTATE_MOUSE_BUTTON = Input.Buttons.RIGHT;
	
	private static final float capsuleHeight = 1.7f;
	private static final float capsuleRadius = .4f;
	
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
	
	private ClosestRayResultCallback resultCallback;
	
	@Editable public float speed = 2; 
	@Editable public float offset = 5; 
	@Editable public boolean clipToWater = true; 
	@Editable public boolean clipToGround = true; 
	@Editable public boolean flyingMode = false; 
	@Editable public boolean useCollider = true; 
	
	@Editable public float smoothing = 10;
	
	private Vector2 dir = new Vector2();
	private Vector2 tan = new Vector2();
	
	private Vector3 focus = new Vector3();
	private Vector3 up = new Vector3();
	private Vector3 rayTarget = new Vector3();

	private Ray ray = new Ray();
	
	private boolean enableControl = true, buttonWasPressed;
	private int prevX, prevY;

	private ImmutableArray<Entity> activeCameras;

	public transient float currentMove;
	public transient boolean onGround;
	
	private Vector3 playerTargetVelocity = new Vector3();
	private Vector3 playerColliderPosition = new Vector3();
	private Matrix4 playerColliderTransform = new Matrix4();
	private btRigidBody playerCollider;
	
	public OpenWorldCameraSystem() {
		super(GamePipeline.INPUT);
	}
	
	public Entity getCameraEntity(){
		return activeCameras.size() > 0 ? activeCameras.first() : null;
	}
	public Camera getCamera(){
		Entity entity = getCameraEntity();
		if(entity == null) return null;
		CameraComponent camera = CameraComponent.components.get(entity);
		return camera.camera;
	}
	
	@Editable
	public void resetPosition(){
		Camera camera = getCamera();
		if(camera != null) camera.position.setZero();
		playerColliderPosition.set(camera.position);
		playerColliderPosition.y = 10; // TODO raycast for ground !
		playerCollider.setWorldTransform(playerColliderTransform.setToTranslation(playerColliderPosition));
		playerCollider.setLinearVelocity(Vector3.Zero);
	}
	
	public void setPosition(Vector3 position) {
		// no need to raycast here because it's recovery from previous saved position.
		playerColliderPosition.set(position);
		playerCollider.setWorldTransform(playerColliderTransform.setToTranslation(playerColliderPosition));
		playerCollider.setLinearVelocity(Vector3.Zero);
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		activeCameras = getEngine().getEntitiesFor(Family.all(OpenWorldCamera.class, CameraComponent.class, ActiveCamera.class).get());
		resultCallback = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
	}
	
	@Override
	public void update(float deltaTime) {
		
		// reset current state
		currentMove = 0;
		onGround = false;
		
		Camera camera = getCamera();
		if(camera == null) return;
		
		if(playerCollider == null){
			playerColliderPosition.set(camera.position);
			playerColliderPosition.y -= offset - capsuleHeight/2;
			playerColliderTransform.setToTranslation(playerColliderPosition);
			playerCollider = (btRigidBody)new BulletBuilder()
					.beginDynamic(playerColliderTransform, 100) // 100 kg
					.capsule(capsuleRadius, capsuleHeight)
					.commit(bulletWorld.collisionWorld);
		}
		
		playerTargetVelocity.setZero();
		
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
				camera.direction.lerp(focus, MathUtils.clamp(deltaTime * smoothing, 0, 1));
				
				up.set(Vector3.Y).rotate(Vector3.X, cameraMatrixProvider.getRoll() * MathUtils.radiansToDegrees);
				
				camera.up.lerp(up, MathUtils.clamp(deltaTime * smoothing, 0, 1));
			}
			else if(Gdx.app.getType() == ApplicationType.Desktop)
			{
				boolean buttonPressed = Gdx.input.isButtonPressed(ROTATE_MOUSE_BUTTON);
				if(!buttonWasPressed && buttonPressed){
					buttonWasPressed = true;
					prevX = Gdx.input.getX();
					prevY = Gdx.input.getY();
					
					focus.set(camera.direction);
					
				}else if(!buttonPressed && buttonWasPressed){
					buttonWasPressed = false;
				}
				if(buttonPressed){
					// camera.direction.set(Vector3.Z);
					float elevation = (float)(Gdx.input.getY() - prevY) / (float)Gdx.graphics.getHeight();
					float angle = (float)(Gdx.input.getX() - prevX) / (float)Gdx.graphics.getWidth() * 2 * 360;
					
					// focus.set(camera.direction);
					focus.rotate(Vector3.Y, -angle);
					focus.y -= elevation * 5;
					focus.nor();
					
					camera.direction.lerp(focus, MathUtils.clamp(deltaTime * smoothing, 0, 1));
					
					prevX = Gdx.input.getX();
					prevY = Gdx.input.getY();
				}
			}
			
			// move camera 2D plan (walking)
			if(!flyingMode){
				dir.set(camera.direction.x, camera.direction.z).nor();
				
				tan.set(-dir.y, dir.x);
				
				playerTargetVelocity.x = dir.x * moveFront + tan.x * moveSide;
				playerTargetVelocity.z = dir.y * moveFront + tan.y * moveSide;
			}
			// move camera 3D space (flying)
			else{
				
				Vector3 tan3d = camera.direction.cpy().crs(camera.up).nor();
				Vector3 nor3d = tan3d.cpy().crs(camera.direction).nor();
				
				playerTargetVelocity.mulAdd(camera.direction, moveFront);
				playerTargetVelocity.mulAdd(tan3d, moveSide);
				playerTargetVelocity.mulAdd(nor3d, moveTop);
			}
		}
		
		if(useCollider){
			
			// update camera
			playerColliderTransform.getTranslation(playerColliderPosition);
			playerColliderPosition.y -= (capsuleHeight - capsuleRadius)/2 - offset;
			camera.position.set(playerColliderPosition);
			
			// update collider speed
			playerTargetVelocity.nor().scl(speed);
			
			// apply gravity by copying Y velocity (if not flying mode)
			Vector3 currentVelocity = playerCollider.getLinearVelocity();
			
			// XXX diving/swiming fixed here, have to sync with player control in game logic ...
			if(flyingMode){
				if(playerColliderPosition.y < env.waterLevel){
					playerTargetVelocity.y += .5f;
					playerCollider.setGravity(new Vector3(0,0,0));
				}else if(playerColliderPosition.y > env.waterLevel + .2f){
					playerTargetVelocity.y = currentVelocity.y;
					playerCollider.setGravity(new Vector3(0,-9.8f,0));
				}
			}else{
				playerCollider.setGravity(new Vector3(0,-9.8f,0));
				playerTargetVelocity.y = currentVelocity.y; // TODO GC ?
				playerTargetVelocity.lerp(currentVelocity, .9f);
			}
			playerCollider.setLinearVelocity(playerTargetVelocity);
			playerCollider.activate(true); // TODO disable desactivation in flags
			playerCollider.setFriction(.5f);
			playerCollider.setRestitution(0);
			
		}else{
			playerColliderPosition.mulAdd(playerTargetVelocity, deltaTime * speed);
			camera.position.set(playerColliderPosition);
			playerColliderTransform.setToTranslation(playerColliderPosition);
		}
		
		// TODO maybe not accurate
		currentMove = playerTargetVelocity.len() * speed * deltaTime;
		
		// ray cast for Y
		if(clipToGround && !useCollider){
			
			ray.origin.set(camera.position);
			ray.origin.y = 0;
			ray.direction.set(0,-1, 0);
			ray.origin.mulAdd(ray.direction, -20f);
			ray.direction.scl(100);
			
			resultCallback.setClosestHitFraction(1e30f);
			resultCallback.setCollisionObject(null);
			bulletWorld.collisionWorld.rayTest(ray.origin, rayTarget.set(ray.origin).add(ray.direction), resultCallback); //
			
			if(resultCallback.hasHit()){
				rayTarget.set(ray.origin).mulAdd(ray.direction, resultCallback.getClosestHitFraction());
				float elevation = rayTarget.y + offset;
				
				if(clipToWater){
					float waterLimit = env.waterLevel + camera.near * (float)Math.sqrt(2);
					elevation = Math.max(elevation, waterLimit);
				}
				if(!flyingMode || elevation > camera.position.y){
					camera.position.y = elevation;
				}
				onGround = !flyingMode;
				
			}
			
		}
		
		// Apply to logic point of view !
		openWorldManager.viewPoint.set(camera.position.x, camera.position.z);
		
		// update
		camera.update();
	}
	
}
