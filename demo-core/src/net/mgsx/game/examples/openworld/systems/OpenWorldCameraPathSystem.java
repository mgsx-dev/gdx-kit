package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.examples.openworld.model.OpenWorldPathBuilder;

@Storable("ow.camera.path")
@EditableSystem
public class OpenWorldCameraPathSystem extends EntitySystem
{
	@Inject OpenWorldGeneratorSystem generator;
	@Inject OpenWorldCameraSystem cameraSystem;
	
	@Editable public float speed = 0.1f;
	@Editable public float distance = 5f;
	@Editable public float offsetMin = 10f;
	@Editable public float offsetRange = 5f;
	
	CatmullRomSpline<Vector3> spline = new CatmullRomSpline<Vector3>();
	Vector3 [] controlPoints;
	float time = 0;

	private float splineLength;
	
	private OpenWorldPathBuilder pathBuilder = new OpenWorldPathBuilder();
	
	public OpenWorldCameraPathSystem() {
		super(GamePipeline.BEFORE_INPUT); // actually before other camera
	}
	
	@Override
	public void update(float deltaTime) 
	{
		Camera camera = cameraSystem.getCamera();
		if(camera == null) return;
		
		if(controlPoints != null) time += deltaTime * Math.abs(speed) / splineLength; // avoid negative
		
		if(time > 1 || controlPoints == null)
		{
			pathBuilder.set(generator, 30, distance, 1);
			pathBuilder.resetLimit().groundMin(offsetMin).groundMax(offsetMin + offsetRange);
			
			if(controlPoints == null){
				controlPoints = new Vector3[4];
				
				pathBuilder.createPath(controlPoints, camera.position);
				
			}else{
				
				pathBuilder.updateDynamicPath(controlPoints);
				
				time -= 1;
			}
			spline.set(controlPoints, false);
			splineLength = spline.approxLength(100); // TODO precision
		}
		
		// quick fix in case of too much time
		time = MathUtils.clamp(time, 0, 1);
		
		spline.valueAt(camera.position, time);
		
		camera.update(); // TODO maybe not necessary ...
	}
}
