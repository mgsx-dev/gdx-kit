package net.mgsx.game.plugins.spline.tools;

import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;

public class BSplineTool extends AbstractPathTool
{
	public BSplineTool(EditorScreen editor) {
		super("BSpline Spline", editor, 4);
	}

	@Override
	protected Path<Vector3> createPath(Vector3[] controls) 
	{
		return new BSpline<Vector3>(controls, 3, false);
	}
	
	

}
