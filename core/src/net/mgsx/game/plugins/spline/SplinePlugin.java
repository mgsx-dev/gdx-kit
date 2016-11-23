package net.mgsx.game.plugins.spline;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.BSpline;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;

import net.mgsx.SplineTest.BlenderCurve;
import net.mgsx.SplineTest.BlenderNURBSCurve;
import net.mgsx.SplineTest.CubicBezierCurve;
import net.mgsx.game.core.GameScreen;
import net.mgsx.game.core.annotations.PluginDef;
import net.mgsx.game.core.plugins.Plugin;
import net.mgsx.game.core.storage.AssetSerializer;
import net.mgsx.game.plugins.spline.components.PathComponent;

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
		
//		engine.assets.setLoader(Path.class, new SynchronousAssetLoader<Path, AssetLoaderParameters<Path>>(new InternalFileHandleResolver()) {
//
//			@Override
//			public Path load(AssetManager assetManager, String fileName, FileHandle file, AssetLoaderParameters<Path> parameter) {
//				Json json = new Json();
//				json.addClassTag("BEZIER", CubicBezierCurve.class);
//				json.addClassTag("NURBS", BlenderNURBSCurve.class);
//				BlenderCurve curve = json.fromJson(BlenderCurve.class, file);
//				return curve.splines.get(0).toPath();
//			}
//
//			@Override
//			public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AssetLoaderParameters<Path> parameter) {
//				return null;
//			}
//		});

//		engine.registry.addSerializer(Path.class, new Serializer<Path>() {
//
//			@Override
//			public void write(Json json, Path object, Class knownType) {
//				// TODO Auto-generated method stub
//			}
//
//			@Override
//			public Path read(Json json, JsonValue jsonData, Class type) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		});
		
		engine.registry.addSerializer(CatmullRomSpline.class, new Serializer<CatmullRomSpline>(){

			@Override
			public void write(Json json, CatmullRomSpline object, Class knownType) {
				json.writeObjectStart();
				json.writeType(CatmullRomSpline.class);
				json.writeValue("controlPoints", object.controlPoints, Vector3[].class);
				json.writeValue("continuous", object.continuous);
				json.writeObjectEnd();
			}

			@Override
			public CatmullRomSpline read(Json json, JsonValue jsonData, Class type) {
				Vector3 [] controlPoints = json.readValue("controlPoints", Vector3[].class, jsonData);
				boolean continuous = json.readValue("continuous", boolean.class, jsonData);
				return new CatmullRomSpline<Vector3>(controlPoints, continuous);
			}
			
		});
		
		engine.registry.addSerializer(BSpline.class, new Serializer<BSpline>(){

			@Override
			public void write(Json json, BSpline object, Class knownType) {
				json.writeObjectStart();
				json.writeType(BSpline.class);
				json.writeValue("controlPoints", object.controlPoints, Vector3[].class);
				json.writeValue("continuous", object.continuous);
				json.writeObjectEnd();
			}

			@Override
			public BSpline read(Json json, JsonValue jsonData, Class type) {
				Vector3 [] controlPoints = json.readValue("controlPoints", Vector3[].class, jsonData);
				boolean continuous = json.readValue("continuous", boolean.class, jsonData);
				return new BSpline<Vector3>(controlPoints, 3, continuous);
			}
			
		});
		
		engine.registry.addSerializer(com.badlogic.gdx.math.CubicBezierCurve.class, new Serializer<com.badlogic.gdx.math.CubicBezierCurve>(){

			@Override
			public void write(Json json, com.badlogic.gdx.math.CubicBezierCurve object, Class knownType) {
				json.writeObjectStart();
				json.writeType(com.badlogic.gdx.math.CubicBezierCurve.class);
				json.writeValue("controlPoints", object.controls, Vector3[].class);
				json.writeObjectEnd();
			}

			@Override
			public com.badlogic.gdx.math.CubicBezierCurve read(Json json, JsonValue jsonData, Class type) {
				Vector3 [] controlPoints = json.readValue("controlPoints", Vector3[].class, jsonData);
				return new com.badlogic.gdx.math.CubicBezierCurve<Vector3>(controlPoints);
			}
			
		});
		
		engine.registry.addSerializer(BlenderCurve.class, new AssetSerializer<BlenderCurve>(BlenderCurve.class));
	}

}
