package net.mgsx.game.examples.openworld.systems;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.core.annotations.Storable;
import net.mgsx.game.core.helpers.systems.TransactionSystem;
import net.mgsx.game.plugins.procedural.model.ClassicalPerlinNoise;

@Storable("ow.camera.trauma")
@EditableSystem
public class OpenWorldCameraTrauma extends TransactionSystem
{
	@Inject OpenWorldCameraSystem cameraSystem;
	
	@Editable public float recoverySpeed = 1;

	@Editable public float speed = 1;
	
	@Editable public float posRange = .1f;
	@Editable public float rotRange = 10;
	
	@Editable(realtime=true) public float trauma = 0;
	
	private ClassicalPerlinNoise noise = new ClassicalPerlinNoise();
	
	private Vector3 vPos = new Vector3();
	
	private float time;
	
	private Vector3 oldCamDir = new Vector3();
	private Vector3 oldCamUp = new Vector3();
	private boolean first = true;
	
	@Editable public void bump(){trauma += .5f;}
	
	public OpenWorldCameraTrauma() {
		super(GamePipeline.BEFORE_INPUT, new AfterSystem(GamePipeline.INPUT){});
		noise.seed(MathUtils.random(Long.MAX_VALUE));
	}
	
	@Override
	protected boolean updateBefore(float deltaTime) {
		if(first){
			first = false;
		}else{
			Camera camera = cameraSystem.getCamera();
			camera.direction.set(oldCamDir);
			camera.up.set(oldCamUp);
			camera.update();
		}
		return true;
	}

	@Override
	protected void updateAfter(float deltaTime) 
	{
		Camera camera = cameraSystem.getCamera();
		
		oldCamDir.set(camera.direction);
		oldCamUp.set(camera.up);
		
		time += speed * deltaTime;
		
		trauma = MathUtils.clamp(trauma, 0, 1);
		trauma = MathUtils.lerp(trauma, 0, deltaTime * recoverySpeed);
		
		float range = trauma * trauma;
		
		noise.get(vPos, time).scl(posRange * range);
		
		float angleX = noise.get(38L, time) * rotRange * range;
		float angleY = noise.get(39L, time) * rotRange * range;
		float angleZ = noise.get(40L, time) * rotRange * range;
		
		camera.position.add(vPos);
		camera.rotate(Vector3.X, angleX);
		camera.rotate(Vector3.Y, angleY);
		camera.rotate(Vector3.Z, angleZ);
		camera.update();
	}
}
