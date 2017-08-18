package net.mgsx.game.examples.voxel.utils;

import com.badlogic.gdx.math.Vector3;

public class Octree 
{
	public static class OctreeNode<T>
	{
		private OctreeNode<T> parent;
		private int childIndex;
		private OctreeNode<T> [] children = new OctreeNode[8];
		
		public OctreeNode<T> neighboor(int dx, int dy, int dz){
			if(dx > 0){
				if((childIndex & 1) == 0){
					return parent.children[childIndex | 1];
				}else{
					return null; // XXX
				}
			}
			return null;
		}
		
		public void generate(Vector3 position, int depth)
		{
			// leaf
			if(depth == 0){
				
			}else{
				
				// generate children
				
				
			}
		}
		
	}
}
