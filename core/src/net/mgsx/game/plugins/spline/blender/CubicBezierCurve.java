package net.mgsx.game.plugins.spline.blender;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

// TODO could be generalized as Path
public class CubicBezierCurve extends AbstractBlenderCurve
{
	public Array<BlenderBezierPoint> points;
	private Float length;
	private boolean lengthKnown;
	private Bezier<Vector3> bezier = new Bezier<Vector3>(new Vector3(), new Vector3(), new Vector3(), new Vector3());
	
	private static Vector3 tmp = new Vector3();
	
	public Vector3 position(Vector3 result, float t)
	{
		// first compute total length
		if(!lengthKnown){
			length = 0f;
			
			for(int i=0 ; i<points.size-2 ; i++)
			{
				BlenderBezierPoint p = points.get(i);
				BlenderBezierPoint p2 = points.get(i+1);
				
				bezier.points.get(0).set(p.co);
				bezier.points.get(1).set(p.hr);
				bezier.points.get(2).set(p2.hl);
				bezier.points.get(3).set(p2.co);
				
				p.offset = length;
				length += bezier.approxLength(10); // TODO configure accuracy
			}
			lengthKnown = true;
		}
		
		// find segment
		float offset = t * length;
		BlenderBezierPoint p = null, p2 = null;
		for(int i=0 ; i<points.size-2 ; i++){
			p = points.get(i);
			p2 = points.get(i+1);
			if(offset >= p.offset && offset < p2.offset) break;
		}
		
		float localT = (offset - p.offset) / (p2.offset - p.offset);
		
		return Bezier.cubic(result, localT, p.co, p.hr, p2.hl, p2.co, tmp);
	}

	@Override
	public Path<Vector3> toPath() {
		Vector3 [] controls = new Vector3[(points.size-1)*3+1];
		for(int i=0, j=0; i<points.size ; i++){
			if(i>0)
				controls[j++] = points.get(i).hl;
			controls[j++] = points.get(i).co;
			if(i<points.size-1)
				controls[j++] = points.get(i).hr;
		}
		return new com.badlogic.gdx.math.CubicBezierCurve<Vector3>(controls);
	}
}