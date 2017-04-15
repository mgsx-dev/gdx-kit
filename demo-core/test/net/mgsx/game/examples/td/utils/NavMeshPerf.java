package net.mgsx.game.examples.td.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;

import net.mgsx.game.examples.td.utils.PerfRunner.PerfTest;

@RunWith(PerfRunner.class)
public class NavMeshPerf {
	NavMesh navMesh;
	Vector3 origin;
	Vector3 destination;
	Vector3 direction = new Vector3(0, 0,-1);
	Array<Vector3> path = new Array<Vector3>();
	
	@Before
	public void setup(){
		G3dModelLoader loader = new G3dModelLoader(new JsonReader());
		ModelData model = loader.parseModel(new FileHandle("test/navmesh.g3dj"));
		navMesh = new NavMesh(model);
		
		origin = new Vector3(.5f,1f,1);
		destination = new Vector3(-3, -2.5f, 1);
		
		navMesh.buildGraph(40);
		
		
	}
	
	@PerfTest(count=100000, timeout=1, memory=(int)2e6)
	public void test()
	{
		path.clear();
		
		boolean r = navMesh.pathFind(origin, destination, direction, path);
		
		Assert.assertTrue(r);
		Assert.assertEquals(6, path.size);
	}
}
