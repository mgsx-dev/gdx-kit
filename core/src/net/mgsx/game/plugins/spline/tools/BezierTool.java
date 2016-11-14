package net.mgsx.game.plugins.spline.tools;

import com.badlogic.gdx.math.CubicBezierCurve;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;

public class BezierTool extends AbstractPathTool
{
	public BezierTool(EditorScreen editor) {
		super("Bezier Spline", editor, 2);
	}

	@Override
	protected Path<Vector3> createPath(Vector3[] controls) 
	{
		return new CubicBezierCurve<Vector3>(controls);
	}
	
	

}
