package net.mgsx.game.examples.openworld.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.examples.openworld.systems.OpenWorldManagerSystem;

public class OpenWorldPathBuilder {

	private OpenWorldManagerSystem manager;
	private float distance;
	private float halfAngleDegree;
	
	private boolean absMin, absMax, gndMin, gndMax;
	private float absMinValue, absMaxValue, gndMinValue, gndMaxValue;
	private float randomness = 1;
	
	public OpenWorldPathBuilder set(OpenWorldManagerSystem manager, float halfAngleDegree, float distance, float randomness) {
		this.manager = manager;
		this.distance = distance;
		this.halfAngleDegree = halfAngleDegree;
		this.randomness = randomness;
		return this;
	}
	
	// TODO set seed ?!
	
	public Vector3 randomXZ(Vector3 v, Vector3 a){
		float angle = MathUtils.random(360) * MathUtils.degreesToRadians;
		v.x = MathUtils.cos(angle) * distance;
		v.z = MathUtils.sin(angle) * distance;
		v.add(a);
		v.y = computeHeight(manager.generateAltitude(v.x, v.z));
		return v;
	}
	
	public Vector3 randomXZ(Vector3 v, Vector3 a, Vector3 b){
		float angle = (MathUtils.random(halfAngleDegree * 2) - halfAngleDegree);
		Vector2 delta = new Vector2(b.x, b.z).sub(a.x, a.z).nor().rotate(angle).scl(distance);
		v.x = b.x + delta.x;
		v.z = b.z + delta.y;
		v.y = computeHeight(manager.generateAltitude(v.x, v.z));
		return v;
	}
	
	float computeHeight(float gndValue)
	{
		float min, max;
		if(gndMin){
			if(absMin){
				min = Math.max(gndMinValue + gndValue, absMinValue);
			}else{
				min = gndMinValue + gndValue;
			}
		}
		else if(absMin){
			min = Math.max(gndValue, absMinValue);
		}else{
			min = gndValue;
		}
		if(gndMax){
			if(absMax){
				max = Math.min(gndMaxValue + gndValue, absMaxValue);
			}else{
				max = gndMaxValue + gndValue;
			}
		}else if(absMax){
			max = Math.min(gndValue, absMaxValue);
		}else{
			max = gndValue;
		}
		// re-clamp min and max to absolute limits
		if(absMax){
			min = Math.min(min, absMaxValue);
		}
		if(absMin){
			max = Math.max(max, absMinValue);
		}
		
		float mid = (min + max) * .5f;
		float randomValue = MathUtils.random(min, max);
		return MathUtils.lerp(mid, randomValue, randomness);
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
		controlPoints[0].y = computeHeight(manager.generateAltitude(initialPosition.x, initialPosition.z));
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

	public OpenWorldPathBuilder resetLimit() 
	{
		absMin = absMax = gndMin = gndMax = false;
		absMinValue = absMaxValue = gndMinValue = gndMaxValue = 0;
		return this;
	}

	public OpenWorldPathBuilder groundMin(float value) {
		gndMin = true;
		gndMinValue = value;
		return this;
	}
	public OpenWorldPathBuilder groundMax(float value) {
		gndMax = true;
		gndMaxValue = value;
		return this;
	}
	public OpenWorldPathBuilder absoluteMin(float value) {
		absMin = true;
		absMinValue = value;
		return this;
	}
	public OpenWorldPathBuilder absoluteMax(float value) {
		absMax = true;
		absMaxValue = value;
		return this;
	}

}
