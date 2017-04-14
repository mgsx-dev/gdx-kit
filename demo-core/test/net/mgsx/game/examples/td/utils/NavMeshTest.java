package net.mgsx.game.examples.td.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;

public class NavMeshTest 
{
	private static void assetEquals(Vector3 expected, Vector3 actual, float epsilon){
		Assert.assertEquals(expected.x, actual.x, epsilon);
		Assert.assertEquals(expected.y, actual.y, epsilon);
		Assert.assertEquals(expected.z, actual.z, epsilon);
	}
	
	private NavMesh navMesh;
	
	@Before
	public void setup(){
		G3dModelLoader loader = new G3dModelLoader(new JsonReader());
		ModelData model = loader.parseModel(new FileHandle("test/navmesh.g3dj"));
		navMesh = new NavMesh(model);
	}
	
	@Test
	public void test()
	{
		float d = navMesh.rayCast(new Ray(new Vector3(0,0,1), Vector3.Z.cpy().scl(-1)));
		
		Assert.assertEquals(1, d, 1e-10f);
		
	}
	@Test
	public void testForwadFlat()
	{
		Vector3 origin = new Vector3(1,1,1);
		Vector3 direction = new Vector3(0,0,-1);
		Vector3 point = new Vector3();
		Vector3 normal = new Vector3();
		
		boolean r = navMesh.rayCast(new Ray(origin, direction), point, normal);
		
		Assert.assertTrue(r);
		Assert.assertEquals(2, origin.dst(point), 1e-5f);
		Assert.assertEquals(new Vector3(0,0,1), normal);
	}
	@Test
	public void testForwadFlat2()
	{
		Vector3 origin = new Vector3(-3,1,1);
		Vector3 direction = new Vector3(0,0,-1);
		Vector3 point = new Vector3();
		Vector3 normal = new Vector3();
		
		boolean r = navMesh.rayCast(new Ray(origin, direction), point, normal);
		
		Assert.assertTrue(r);
		Assert.assertEquals(1, origin.dst(point), 1e-5f);
		Assert.assertEquals(new Vector3(0,0,1), normal);
	}
	@Test
	public void testForwadNonFlat()
	{
		Vector3 origin = new Vector3(-1,1,1);
		Vector3 direction = new Vector3(0,0,-1);
		Vector3 point = new Vector3();
		Vector3 normal = new Vector3();
		
		boolean r = navMesh.rayCast(new Ray(origin, direction), point, normal);
		
		Assert.assertTrue(r);
		Assert.assertEquals(1.5f, origin.dst(point), 1e-5f);
		assetEquals(new Vector3(0.44f,0,0.89f), normal, 1e-2f);
	}
	
	@Test
	public void testGraphNoAngle()
	{
		Vector3 origin = new Vector3(.5f,.5f,1);
		Vector3 destination = new Vector3(-2.5f, .5f, 1);
		
		navMesh.buildGraph(180);
		
		Array<Vector3> path = new Array<Vector3>();
		boolean r = navMesh.pathFind(origin, destination, new Vector3(0, 0,-1), path );
		
		Assert.assertTrue(r);
		Assert.assertEquals(3, path.size);
		
	}
	@Test
	public void testGraphWithAngleFail()
	{
		Vector3 origin = new Vector3(1f,.5f,1);
		Vector3 destination = new Vector3(1f, -.5f, 1);
		
		navMesh.buildGraph(40);
		
		Array<Vector3> path = new Array<Vector3>();
		boolean r = navMesh.pathFind(origin, destination, new Vector3(0, 0,-1), path );
		
		Assert.assertFalse(r);
	}
	@Test
	public void testGraphWithAnglePass()
	{
		Vector3 origin = new Vector3(1f,.5f,1);
		Vector3 destination = new Vector3(1f, -.5f, 1);
		
		navMesh.buildGraph(50);
		
		Array<Vector3> path = new Array<Vector3>();
		boolean r = navMesh.pathFind(origin, destination, new Vector3(0, 0,-1), path );
		
		Assert.assertTrue(r);
		Assert.assertEquals(1, path.size);
	}
	@Test
	public void testGraphWithAngle()
	{
		Vector3 origin = new Vector3(.5f,1f,1);
		Vector3 destination = new Vector3(-3, -2.5f, 1);
		
		navMesh.buildGraph(40);
		
		Array<Vector3> path = new Array<Vector3>();
		boolean r = navMesh.pathFind(origin, destination, new Vector3(0, 0,-1), path );
		
		Assert.assertTrue(r);
		Assert.assertEquals(6, path.size);
	}



}
