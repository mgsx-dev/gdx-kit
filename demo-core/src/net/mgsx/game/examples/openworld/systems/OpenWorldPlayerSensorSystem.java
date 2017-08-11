package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.examples.openworld.components.ObjectMeshComponent;
import net.mgsx.game.examples.openworld.model.OpenWorldElement;
import net.mgsx.game.examples.openworld.utils.BulletBuilder;
import net.mgsx.game.examples.openworld.utils.DeltaSet;
import net.mgsx.game.plugins.bullet.system.BulletWorldSystem;

/**
 * Just a POC of sensor : could be used to know if there is a sleepable area around ...etc
 * with delta set, it is possible to trigger events like "a sleepable item is nearby now" and then enable
 * some HUD commands instead of require to peek the object to sleep.
 * It could be used to detect dangerous animal around (requiring multiple sphere in the sensor)
 * we could have : short (0-2m), medium (2-10m), far(10-100m) ranges with 3 spheres.
 * 
 * @author mgsx
 *
 */
public class OpenWorldPlayerSensorSystem extends EntitySystem
{
	@Inject BulletWorldSystem bulletSystem;
	@Inject OpenWorldCameraSystem cameraSystem;
	
	private Vector3 sensorPosition = new Vector3();
	private DeltaSet<OpenWorldElement> deltaElements = new DeltaSet<OpenWorldElement>();
	private Matrix4 sensorTransform = new Matrix4();
	private btCollisionObject sensorBody;
	private ContactResultCallback sensorCallback = new ContactResultCallback(){
		public float addSingleResult(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0, btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
			Object userObject = null;
			if(colObj0Wrap.getCollisionObject() == sensorBody){
				userObject = colObj1Wrap.getCollisionObject().userData;
			}else if(colObj1Wrap.getCollisionObject() == sensorBody){
				userObject = colObj0Wrap.getCollisionObject().userData;
			}
			if(userObject instanceof Entity){
				ObjectMeshComponent omc = ObjectMeshComponent.components.get((Entity)userObject);
				if(omc != null){
					deltaElements.add(omc.userObject.element);
				}
			}
			return 0; // not used
		};
	};
	
	public OpenWorldPlayerSensorSystem() {
		super(GamePipeline.AFTER_PHYSICS);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		sensorBody = new BulletBuilder()
				.beginKinematic(new Matrix4())
				.sphere(2)
				.end();
	}
	
	@Override
	public void update(float deltaTime) {
		
		Camera camera = cameraSystem.getCamera();
		if(camera == null) return;
		
		deltaElements.reset();
		sensorPosition.set(camera.position);
		sensorPosition.y -= cameraSystem.offset;
		sensorBody.setWorldTransform(sensorTransform.setToTranslation(sensorPosition));
		bulletSystem.collisionWorld.contactTest(sensorBody, sensorCallback);
		for(OpenWorldElement e : deltaElements.addedItems()){
			System.out.println("added : " + e.type);
		}
		for(OpenWorldElement e : deltaElements.removedItems()){
			System.out.println("removed : " + e.type);
		}
	}
}
