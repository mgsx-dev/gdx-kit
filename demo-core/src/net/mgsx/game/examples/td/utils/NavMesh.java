package net.mgsx.game.examples.td.utils;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

public class NavMesh {

	private static class Edge implements Connection<TriNode>{

		TriNode a, b;
		
		
		public Edge(TriNode a, TriNode b) {
			super();
			this.a = a;
			this.b = b;
		}

		@Override
		public float getCost() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public TriNode getFromNode() {
			return a;
		}

		@Override
		public TriNode getToNode() {
			return b;
		}
		
	}
	
	public static class TriNode implements IndexedNode<TriNode>{
		public Array<Connection<TriNode>> neighboors = new Array<Connection<TriNode>>();
		public int index;
		public Vector3 position = new Vector3();
		public Vector3 normal = new Vector3();
		
		public TriNode(int index) {
			super();
			this.index = index;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public Array<Connection<TriNode>> getConnections() {
			return neighboors;
		}
	}
	
	private static class EdgeNode{
		Array<TriNode> triangles = new Array<TriNode>();
	}
	
	private IntMap<IntMap<EdgeNode>> edges;
	
	public TriNode[] triNodes;
	
	private IndexedGraph<TriNode> graph;
	
	float [] vertices;
	short [] indices;
	int vertexSize = 0;
	
	private IndexedAStarPathFinder<TriNode> apf;
	
	Heuristic<TriNode> heuristic = new Heuristic<NavMesh.TriNode>() {
		
		@Override
		public float estimate(TriNode node, TriNode endNode) {
			return node.position.dst(endNode.position);
		}
	};

	GraphPath<Connection<TriNode>> outPath = new DefaultGraphPath<Connection<TriNode>>();

	
	public NavMesh(ModelData model) {
		super();
		vertices = model.meshes.get(0).vertices;
		indices = model.meshes.get(0).parts[0].indices;
		vertexSize = 0;
		for(VertexAttribute attr : model.meshes.get(0).attributes){
			vertexSize += attr.numComponents;
		}
	}
	
	public NavMesh(Model model) {
		super();
		vertexSize = model.meshes.get(0).getVertexSize() / 4;
		model.meshes.get(0).getVertices(vertices = new float[model.meshes.get(0).getNumVertices() * vertexSize]);
		model.meshes.get(0).getIndices(indices = new short[model.meshes.get(0).getNumIndices()]);
	}
	
	
	
	public void buildGraph(float maxAngle)
	{
		float cosAlpha = (float)Math.cos(maxAngle * MathUtils.degreesToRadians);

		// expect normal smoothing
		// build edges
		int triangles = indices.length / 3; // 32 (4x4x2)
		int vcount = vertices.length / vertexSize; // 58 (5x5 !) => (5x5)
		
		triNodes = new TriNode[triangles];
		edges = new IntMap<IntMap<EdgeNode>>();
		
		Vector3 t1 = new Vector3();
		Vector3 t2 = new Vector3();
		Vector3 t3 = new Vector3();
		Vector3 tmp = new Vector3();
		for(int i=0, tri=0 ; i<indices.length ; i+=3, tri++)
		{
			triNodes[tri] = new TriNode(tri);
			
		
			
			int i1 = indices[i+0];
			int i2 = indices[i+1];
			int i3 = indices[i+2];
			
			addEdge(tri, i1, i2);
			addEdge(tri, i2, i3);
			addEdge(tri, i3, i1);
			
			int iv1 = i1 * vertexSize;
			int iv2 = i2 * vertexSize;
			int iv3 = i3 * vertexSize;
			
			t1.set(vertices[iv1+0], vertices[iv1+1], vertices[iv1+2]);
			t2.set(vertices[iv2+0], vertices[iv2+1], vertices[iv2+2]);
			t3.set(vertices[iv3+0], vertices[iv3+1], vertices[iv3+2]);
			
			triNodes[tri].position.setZero();
			triNodes[tri].position.add(vertices[iv1+0], vertices[iv1+1], vertices[iv1+2]);
			triNodes[tri].position.add(vertices[iv2+0], vertices[iv2+1], vertices[iv2+2]);
			triNodes[tri].position.add(vertices[iv3+0], vertices[iv3+1], vertices[iv3+2]);
			triNodes[tri].position.scl(1f/3f);
			
			triNodes[tri].normal.set(t2).sub(t1);
			tmp.set(t3).sub(t1);
			triNodes[tri].normal.crs(tmp).nor();
		}
		
		
		for(Entry<IntMap<EdgeNode>> e1 : edges){
			for(Entry<EdgeNode> e2 : e1.value){
				for(TriNode tri : (TriNode[])e2.value.triangles.toArray(TriNode.class)){
					for(TriNode tri2 : e2.value.triangles){
						if(tri2 != tri && tri.normal.dot(tri2.normal) >= cosAlpha){
							tri2.neighboors.add(new Edge(tri2, tri));
							tri.neighboors.add(new Edge(tri, tri2));
						}
					}
				}
			}
		}
		
		graph = new DefaultIndexedGraph<TriNode>(new Array<TriNode>(triNodes));

		apf = new IndexedAStarPathFinder<TriNode>(graph);
	}
	
	public boolean pathFind(Vector3 origin, Vector3 destination, Vector3 up, Array<Vector3> path){
		return pathFind(triNodeCast(origin, up), triNodeCast(destination, up), path);
	}
	public boolean pathFind(int srcIndex, int dstIndex, Array<Vector3> path){
		return pathFind(triNodes[srcIndex], triNodes[dstIndex], path);
	}
	private boolean pathFind(TriNode a, TriNode b, Array<Vector3> path){
		
		if(a == null || b == null) return false;
		
		outPath.clear();
		
		if(apf.searchConnectionPath(a, b, heuristic, outPath )){
			for(Connection<TriNode> cnx : outPath){
				path.add(cnx.getToNode().position);
			}
			return true;
		}
		return false;
	}
	
	private static Ray ray = new Ray();
	private static Vector3 normal = new Vector3();
	private static Vector3 nearest = new Vector3();
	
	public int triCast(Vector3 origin, Vector3 direction) {
		TriNode tn = triNodeCast(origin, direction);
		return tn == null ? -1 : tn.index;
	}
	private TriNode triNodeCast(Vector3 origin, Vector3 direction) 
	{
		// TODO Auto-generated method stub
		int i = triRayCast(ray.set(origin, direction), nearest, normal);
		if(i < 0) return null;
		return triNodes[i];
	}

	private void addEdge(int tri, int i1, int i2) {
		if(i1 > i2){
			int tmp = i1;
			i1 = i2;
			i2 = tmp;
		}
		IntMap<EdgeNode> e1 = edges.get(i1);
		if(e1 == null) edges.put(i1, e1 = new IntMap<EdgeNode>());
		EdgeNode e2 = e1.get(i2);
		if(e2 == null) e1.put(i2, e2 = new EdgeNode());
		e2.triangles.add(triNodes[tri]);
	}

	public float rayCast(Ray ray){
		
		boolean intersect = Intersector.intersectRayTriangles(ray, vertices, indices, vertexSize, intersection);
		
		if(intersect){
			return ray.origin.dst(intersection);
		}
		return -1;
		
	}
	
	
	public boolean rayCast(Ray ray, Vector3 nearest, Vector3 normal){
		return triRayCast(ray, nearest, normal) >= 0;
	}
	
	Vector3 t1 = new Vector3();
	Vector3 t2 = new Vector3();
	Vector3 t3 = new Vector3();
	Vector3 tmp = new Vector3();
	Vector3 intersection = new Vector3();

	private int triRayCast(Ray ray, Vector3 nearest, Vector3 normal){
		
		float distance = -1;
		int triangle = -1;
		for(int i=0, tri=0 ; i<indices.length ; i+=3, tri++)
		{
			int i1 = indices[i+0] * vertexSize;
			int i2 = indices[i+1] * vertexSize;
			int i3 = indices[i+2] * vertexSize;
			t1.set(vertices[i1+0], vertices[i1+1], vertices[i1+2]);
			t2.set(vertices[i2+0], vertices[i2+1], vertices[i2+2]);
			t3.set(vertices[i3+0], vertices[i3+1], vertices[i3+2]);
			
			
			if(Intersector.intersectRayTriangle(ray, t1, t2, t3, intersection)){
				float dst = intersection.dst2(ray.origin);
				if(triangle < 0 || distance > intersection.dst2(ray.origin)){
					triangle = tri;
					nearest.set(intersection);
					distance = dst;
					normal.set(t2).sub(t1);
					tmp.set(t3).sub(t1);
					normal.crs(tmp).nor();
				}
			}
		}
		
		return triangle;
	}
	
	
}
