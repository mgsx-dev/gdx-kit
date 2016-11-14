package net.mgsx;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class SplineTest {

	// TODO use Blender name and Blender model
	public static class BlenderCurve
	{
		public String name;
		public Array<AbstractBlenderCurve> splines;
		
		
		
	}
	
	public abstract static class AbstractBlenderCurve{
		abstract public Path<Vector3> toPath();
	}
	
	public static class BlenderBezierPoint
	{
		public Vector3 hl, co, hr;
		float offset;
	}
	
	public static class BlenderNURBSCurve extends AbstractBlenderCurve
	{
		public Vector3[] points;
		public BSpline<Vector3> bs;
		@Override
		public Path<Vector3> toPath() {
			return new BSpline<Vector3>(points, 3, false);
		}
	}
	
	// TODO could be generalized as Path
	public static class CubicBezierCurve extends AbstractBlenderCurve
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
	
	public static void main(String[] args) throws FileNotFoundException 
	{
		Json json = new Json();
		json.addClassTag("BEZIER", CubicBezierCurve.class);
		json.addClassTag("NURBS", BlenderNURBSCurve.class);
		json.setTypeName("type");
		Array<BlenderCurve> all = json.fromJson(Array.class, BlenderCurve.class, new FileReader("/home/germain/dev/box2d-editor/presets/bones/splines.json"));
	
		
		System.out.println(all.get(0).name);
		CubicBezierCurve bzc = (CubicBezierCurve)all.get(0).splines.get(0);
		System.out.println(bzc.points.get(0).co.x);
		
		Vector3 v = bzc.position(new Vector3(), 0.0f);
		System.out.println(v);
		
		System.out.println(all.get(1).name);
		BlenderNURBSCurve bsc = (BlenderNURBSCurve)all.get(1).splines.get(0);
		System.out.println(bsc.points[0].x);
		
		
	}

}
