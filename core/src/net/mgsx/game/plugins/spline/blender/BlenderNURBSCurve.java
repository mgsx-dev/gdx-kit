package net.mgsx.game.plugins.spline.blender;

import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;

public class BlenderNURBSCurve extends AbstractBlenderCurve
{
	public Vector3[] points;
	public BSpline<Vector3> bs;
	@Override
	public Path<Vector3> toPath() {
		return new BSpline<Vector3>(points, 3, false);
	}
}