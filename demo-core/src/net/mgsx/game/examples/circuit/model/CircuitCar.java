package net.mgsx.game.examples.circuit.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CircuitCar {
	public Vector2 uvPos = new Vector2(), uvDir = new Vector2();
	public float angle;
	
	public Vector3 position = new Vector3();
	public Vector3 normal = new Vector3();
	
	public float prefSpeed;
	public float speed;
	public float busy;
	
	public CircuitTrack track;
	public CircuitTrack trackB;
	public float transition;
	public Vector3 direction = new Vector3();
	
	public ModelInstance model;
	
	
	
	public CircuitCar(CircuitTrack track) {
		super();
		this.track = track;
	}



	public void update(float delta){
		
		// TODO get car just front
		CircuitCar carFront = track.getCarFront(this);
		float dist = carFront.position.dst(position);
		if(dist < 1){
			speed = 0;
		}else if(dist < 3){
			speed = MathUtils.lerp(speed, 0, delta / dist);
			busy += delta * (prefSpeed - speed);
		}else{
			speed = MathUtils.lerp(speed, prefSpeed, delta);
		}
		if(speed < prefSpeed)
			busy = MathUtils.lerp(1, busy,  speed / prefSpeed);
		else
			busy = MathUtils.lerp(busy, 0, delta);
		if(busy > 1 && trackB == null){
			CircuitTrack fastTrack = track.getFastTrack();
			if(fastTrack != null){
				fastTrack.cars.add(this);
				trackB = fastTrack;
				transition = 0;
				busy = 0;
			}
			// if(track.getLeft().canInsert(this))
			// TODO change track
		}
		
		if(trackB != null){
			transition += delta;
			if(transition > 1){
				transition = 1;
				track.cars.removeValue(this, true);
				track = trackB;
				trackB = null;
				uvPos.y = track.v + track.vLen/2;
			}else{
				uvPos.y = MathUtils.lerp(track.v + track.vLen/2, trackB.v + trackB.vLen/2, Interpolation.sine.apply(transition));
			}
		}
		
	}
}
