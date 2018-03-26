package net.mgsx.game.examples.circuit.model;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class CircuitModel {

	public Array<Vector2> dots = new Array<Vector2>();

	public Array<CircuitCar> cars = new Array<CircuitCar>();
	public Array<CircuitTrack> tracks = new Array<CircuitTrack>();
	
	public boolean valid = false;
	
	public void invalidate() {
		valid = false;
	}
	
	public void update(CatmullRomSpline<Vector3> spline, float delta){
		for(CircuitTrack track : tracks){
			track.update(delta);
		}
		float speedFactor = .1f;
		for(CircuitCar car : cars){
			car.update(delta);
			float len = spline.derivativeAt(car.direction, car.uvPos.x).len();
			if(car.track.ccw)
				car.uvPos.x = (car.uvPos.x + speedFactor * car.speed * delta / len + 1f) % 1f;
			else
				car.uvPos.x = ((car.uvPos.x - speedFactor * car.speed * delta / len + 1f) % 1f + 1f) % 1f;
			car.normal.set(Vector3.Z).crs(car.direction.nor()).nor();
			spline.valueAt(car.position, car.uvPos.x).mulAdd(car.normal, ((car.uvPos.y) * 2 - 1) * 10);
			
			//.setToLookAt(car.direction, car.normal)
			car.model.transform.idt().translate(car.position).rotate(Vector3.Z, -90).mul(new Matrix4().setToLookAt(car.normal, Vector3.Z));
			car.model.calculateTransforms();
		}
		
	}
}
