package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.components.OpenWorldCamera;
import net.mgsx.game.plugins.camera.components.CameraComponent;

@Storable("ow.camera.path")
@EditableSystem
public class OpenWorldCameraPathSystem extends EntitySystem
{
	@Inject OpenWorldManagerSystem manager;
	
	@Editable public float speed = 0.1f;
	@Editable public float distance = 5f;
	@Editable public float offset = 10f;
	
	CatmullRomSpline<Vector3> spline = new CatmullRomSpline<Vector3>();
	Vector3 [] controlPoints;
	float time = 0;

	private float splineLength;
	
	public OpenWorldCameraPathSystem() {
		super(GamePipeline.BEFORE_INPUT); // actually before other camera
	}
	
	// TODO cache
	private Camera getCamera(){
		ImmutableArray<Entity> cameras = getEngine().getEntitiesFor(Family.all(OpenWorldCamera.class, CameraComponent.class).get());
		if(cameras.size() == 0) return null;
		
		Entity entity = cameras.first();
		CameraComponent camera = CameraComponent.components.get(entity);
		return camera.camera;
	}
	
	private Vector3 random(Vector3 v, Vector3 a){
		float angle = (MathUtils.random(60) - 30) * MathUtils.degreesToRadians;
		v.x = MathUtils.cos(angle) * distance;
		v.z = MathUtils.sin(angle) * distance;
		v.add(a);
		return v;
	}
	
	private Vector3 random(Vector3 v, Vector3 a, Vector3 b){
		float angle = (MathUtils.random(60) - 30);
		Vector2 delta = new Vector2(b.x, b.z).sub(a.x, a.z).nor().rotate(angle).scl(distance);
		v.x = b.x + delta.x;
		v.z = b.z + delta.y;
		return v;
	}
	
	@Override
	public void update(float deltaTime) 
	{
		Camera camera = getCamera();
		if(camera == null) return;
		
		if(controlPoints != null) time += deltaTime * speed / splineLength;
		// TODO update camera from path (position only)
		
		if(time > 1 || controlPoints == null)
		{
			if(controlPoints == null){
				controlPoints = new Vector3[4];
				for(int i=0 ; i<controlPoints.length ; i++){
					controlPoints[i] = new Vector3();
				}
				
				// TODO not really true
				controlPoints[0].set(camera.position);
				random(controlPoints[1], controlPoints[0]);
				random(controlPoints[2], controlPoints[0], controlPoints[1]);
				random(controlPoints[3], controlPoints[1], controlPoints[2]);
				for(int i=0 ; i<controlPoints.length ; i++){
					controlPoints[i].y = manager.generate(controlPoints[i].x, controlPoints[i].z) + offset;
				}
			}else{
				// rotate buffer
				Vector3 lastPoint = controlPoints[0];
				for(int i=1 ; i<controlPoints.length ; i++) controlPoints[i-1] = controlPoints[i];
				controlPoints[controlPoints.length-1] = lastPoint;
				
				// TODO get next control points based on last ones
				
				random(lastPoint, 
						controlPoints[controlPoints.length-3], 
						controlPoints[controlPoints.length-2]);
				
				lastPoint.y = manager.generate(lastPoint.x, lastPoint.z) + offset;
				
				time -= 1;
			}
			spline.set(controlPoints, false);
			splineLength = spline.approxLength(100); // TODO precision
		}
		
		// TODO when time reach the end (over) then rotate control points
		
		spline.valueAt(camera.position, time);
		
		// TODO set camera
		camera.update(); // TODO maybe not necessary ...
		
		
	}
}
