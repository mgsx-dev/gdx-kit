package net.mgsx.game.plugins.spline;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import net.mgsx.SplineTest.BlenderCurve;
import net.mgsx.SplineTest.BlenderNURBSCurve;
import net.mgsx.SplineTest.CubicBezierCurve;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.AssetSerializer;
import net.mgsx.game.core.storage.Storage;

@PluginDef(components={PathComponent.class})
public class SplinePlugin implements Plugin
{

	@Override
	public void initialize(GameScreen engine) 
	{
		// TODO use an asset loader ??
		engine.assets.setLoader(BlenderCurve.class, new SynchronousAssetLoader<BlenderCurve, AssetLoaderParameters<BlenderCurve>>(new InternalFileHandleResolver()) {

			@Override
			public BlenderCurve load(AssetManager assetManager, String fileName, FileHandle file, AssetLoaderParameters<BlenderCurve> parameter) {
				Json json = new Json();
				json.addClassTag("BEZIER", CubicBezierCurve.class);
				json.addClassTag("NURBS", BlenderNURBSCurve.class);
				// json.setTypeName("class");
				return json.fromJson(BlenderCurve.class, file);
			}

			@Override
			public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AssetLoaderParameters<BlenderCurve> parameter) {
				return null;
			}
		});
		
		Storage.register(new AssetSerializer<BlenderCurve>(BlenderCurve.class));
	}

}
