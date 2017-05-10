package net.mgsx.game.examples.tactics.util;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;

public class Voronoi2D 
{
	public static class VoronoiResult
	{
		public float id;
		public float[] f = new float[9];
		public float x, y;
	}
	private static class Node
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
				random.setSeed(seed + (long)cy);
				random.setSeed(random.nextLong() + (long)cx);
				Node node = nodes[c++];
				node.x = cx + random.nextFloat();
				node.y = cy + random.nextFloat();
				node.index = random.nextFloat();
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
}
