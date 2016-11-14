package net.mgsx.game.plugins.spline.tools;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.game.core.EditorScreen;

public class CatmullRomTool extends AbstractPathTool
{
	public CatmullRomTool(EditorScreen editor) {
		super("CatmullRom Spline", editor, 4);
	}

	@Override
	protected Path<Vector3> createPath(Vector3[] controls) 
	{
		return new CatmullRomSpline<Vector3>(controls, false);
	}
	
	

}
