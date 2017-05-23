package net.mgsx.game.examples.tactics.util;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;

public class Voronoi2D 
{
	public static class VoronoiResult
	{
		public float id;
		public float[] f = new float[9];
		public float x, y;
	}
	public static class Node
	{
		public float index;
		public float x, y;
		public float distance;
	}
	
	private VoronoiResult result = new VoronoiResult();
	private RandomXS128 random = new RandomXS128();
	private long seed;
	private Node[] nodes;
	private Comparator<Node> comparator = new Comparator<Voronoi2D.Node>() {
		@Override
		public int compare(Node o1, Node o2) {
			return Float.compare(o1.distance, o2.distance);
		}
	};
	
	public Voronoi2D() {
		nodes = new Node[9];
		for(int i=0 ; i<nodes.length ; i++) nodes[i] = new Node();
	}
	
	public void setSeed(long seed){
		this.seed = seed;
	}
	
	public VoronoiResult generate(float x, float y, int layers)
	{
		int ix = MathUtils.floor(x);
		int iy = MathUtils.floor(y);
		
		int c=0;
		for(int i=-1 ; i<2 ; i++){
			int cy = iy + i;
			for(int j=-1 ; j<2 ; j++){
				int cx = ix + j;
				Node node = generateNode(nodes[c++], cx, cy);
				float dx = node.x - x;
				float dy = node.y - y;
				node.distance = (float)Math.sqrt(dx*dx+dy*dy);
			}
		}
		
		Arrays.sort(nodes, comparator);
		
		result.id = nodes[0].index;
		result.x = nodes[0].x;
		result.y = nodes[0].y;
		for(int i=0 ; i<nodes.length ; i++){
			result.f[i] = nodes[i].distance * 1f;
		}
		
		return result;
	}

	public void query(Array<Node> results, float x1, float y1, float x2, float y2) 
	{
		int ix1 = MathUtils.floor(x1);
		int iy1 = MathUtils.floor(y1);
		int ix2 = MathUtils.floor(x2);
		int iy2 = MathUtils.floor(y2);
		
		for(int i=iy1 ; i<=iy2 ; i++){
			for(int j=ix1 ; j<=ix2 ; j++){
				results.add(generateNode(new Node(), j, i));
			}
		}
	}

	private Node generateNode(Node node, int x, int y) 
	{
		random.setSeed(seed + (long)y);
		random.setSeed(random.nextLong() + (long)x);
		node.x = x + random.nextFloat();
		node.y = y + random.nextFloat();
		node.index = random.nextFloat();
		return node;
	}
}
