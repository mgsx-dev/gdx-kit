package com.badlogic.gdx.math;

public class CubicBezierCurve<T extends Vector<T>> implements Path<T>
{
	private T tmp;
	
	private T [] controls;
	
	public CubicBezierCurve(T [] controls) 
	{
		super();
		this.controls = controls;
		tmp = controls[0].cpy();
	}

	@Override
	public T derivativeAt(T out, float t) 
	{
		int segments = (controls.length - 1) / 3;
		
		int segmentIndex = MathUtils.floor(t * segments);
		
		if(segmentIndex >= segments){
			out.set(controls[controls.length-1]);
		}
		
		float tAtIndex = segmentIndex / (float)segments;
		float tAtNextIndex = (segmentIndex+1) / (float)segments;
		float localT = (t - tAtIndex) / (tAtNextIndex - tAtIndex);
		
		int index = segmentIndex * 3;
		
		T p0 = controls[index];
		if(index + 1 >= controls.length)
			return out.setZero(); // ???
		
		T p1 = controls[index+1];
		if(index + 2 >= controls.length)
			return Bezier.linear_derivative(out, localT, p0, p1, tmp);
		
		T p2 = controls[index+2];
		if(index + 3 >= controls.length)
			return Bezier.quadratic_derivative(out, localT, p0, p1, p2, tmp);
			
		T p3 = controls[index+3];
		return Bezier.cubic_derivative(out, localT, p0, p1, p2, p3, tmp);
	}

	@Override
	public T valueAt(T out, float t) 
	{
		int segments = (controls.length - 1) / 3;
		
		int segmentIndex = MathUtils.floor(t * segments);
		
		if(segmentIndex >= segments){
			out.set(controls[controls.length-1]);
		}
		
		float tAtIndex = segmentIndex / (float)segments;
		float tAtNextIndex = (segmentIndex+1) / (float)segments;
		float localT = (t - tAtIndex) / (tAtNextIndex - tAtIndex);
		
		int index = segmentIndex * 3;
		
		T p0 = controls[index];
		if(index + 1 >= controls.length)
			return out.set(p0);
		
		T p1 = controls[index+1];
		if(index + 2 >= controls.length)
			return Bezier.linear(out, localT, p0, p1, tmp);
		
		T p2 = controls[index+2];
		if(index + 3 >= controls.length)
			return Bezier.quadratic(out, localT, p0, p1, p2, tmp);
			
		T p3 = controls[index+3];
		return Bezier.cubic(out, localT, p0, p1, p2, p3, tmp);
	}

	@Override
	public float approximate(T v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float locate(T v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float approxLength(int samples) {
		// TODO Auto-generated method stub
		return 0;
	}

}
