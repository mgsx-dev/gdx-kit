package net.mgsx;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.mgsx.game.plugins.spline.blender.BlenderCurve;
import net.mgsx.game.plugins.spline.blender.BlenderNURBSCurve;
import net.mgsx.game.plugins.spline.blender.CubicBezierCurve;

public class SplineTest {

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
