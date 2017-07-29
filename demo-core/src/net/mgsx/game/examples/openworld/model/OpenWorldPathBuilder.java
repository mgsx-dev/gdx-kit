package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.examples.openworld.systems.OpenWorldManagerSystem;

public class OpenWorldPathBuilder {

	private OpenWorldManagerSystem manager;
	private float distance, offset;
	private float halfAngleDegree;
	
	public OpenWorldPathBuilder set(OpenWorldManagerSystem manager, float halfAngleDegree, float distance, float offset) {
		this.manager = manager;
		this.distance = distance;
		this.offset = offset;
		this.halfAngleDegree = halfAngleDegree;
		return this;
	}
	
	// TODO set seed ?!
	
	public Vector3 randomXZ(Vector3 v, Vector3 a){
		float angle = MathUtils.random(360) * MathUtils.degreesToRadians;
		v.x = MathUtils.cos(angle) * distance;
		v.z = MathUtils.sin(angle) * distance;
		v.add(a);
		v.y = manager.generate(v.x, v.z) + offset;
		return v;
	}
	
	public Vector3 randomXZ(Vector3 v, Vector3 a, Vector3 b){
		float angle = (MathUtils.random(halfAngleDegree * 2) - halfAngleDegree);
		Vector2 delta = new Vector2(b.x, b.z).sub(a.x, a.z).nor().rotate(angle).scl(distance);
		v.x = b.x + delta.x;
		v.z = b.z + delta.y;
		v.y = manager.generate(v.x, v.z) + offset;
		return v;
	}
	
	
	/**
	 * Generate an absolute path relative to the ground.
	 * @param controlPoints
	 */
	public void createPath(Vector3 [] controlPoints, Vector3 initialPosition)
	{
		for(int i=0 ; i<controlPoints.length ; i++){
			if(controlPoints[i] == null) controlPoints[i] = new Vector3();
		}
		controlPoints[0].set(initialPosition);
		randomXZ(controlPoints[1], controlPoints[0]);
		for(int i=2 ; i<controlPoints.length ; i++){
			randomXZ(controlPoints[i], controlPoints[i-2], controlPoints[i-1]);
		}
	}

	public void updateDynamicPath(Vector3 [] controlPoints)
	{
		// rotate buffer
		Vector3 lastPoint = controlPoints[0];
		for(int i=1 ; i<controlPoints.length ; i++) controlPoints[i-1] = controlPoints[i];
		controlPoints[controlPoints.length-1] = lastPoint;
		
		// generate new point
		randomXZ(lastPoint, 
				controlPoints[controlPoints.length-3], 
				controlPoints[controlPoints.length-2]);
	}

}
