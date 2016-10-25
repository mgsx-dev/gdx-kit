package net.mgsx.game.plugins.spline;

import net.mgsx.SplineTest.BlenderNURBSCurve;
import net.mgsx.SplineTest.CubicBezierCurve;
import net.mgsx.game.core.GameEngine;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.Storage;

public class SplinePlugin implements Plugin
{

	@Override
	public void initialize(GameEngine engine) 
	{
		// TODO use an asset loader ??
//		engine.assets.setLoader(BlenderCurve.class, new AssetLoader<BlenderCurve, AssetLoaderParameters<BlenderCurve>>(new InternalFileHandleResolver()) {
//			@Override
//			public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
//					AssetLoaderParameters<BlenderCurve> parameter) {
//				return null;
//			}
//		});
//		
		Storage.addClassTag("BEZIER", CubicBezierCurve.class);
		Storage.addClassTag("NURBS", BlenderNURBSCurve.class);
		
	}

}
