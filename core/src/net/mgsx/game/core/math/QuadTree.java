package net.mgsx.game.core.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class QuadTree<T> {

	Vector2 minSize = new Vector2();
	private Array<T> result = new Array<T>();
	private int ix, iy, iw, ih;
	private int bx, by, bw, bh;
	private Node<T> root;
	
	private static class Node<T>{
		Node<T> n00, n10, n01, n11;
		final Array<T> elements = new Array<T>();
		@Override
		public String toString() {
			return toString("");
		}
		public String toString(String prefix) {
			String s = prefix + "(" + elements.toString() + ")";
			String nextPrefix = prefix + "  ";
			if(n00 != null) s += "\n" + prefix + "n00" + n00.toString(nextPrefix);
			if(n10 != null) s += "\n" + prefix + "n10" + n10.toString(nextPrefix);
			if(n01 != null) s += "\n" + prefix + "n01" + n01.toString(nextPrefix);
			if(n11 != null) s += "\n" + prefix + "n11" + n11.toString(nextPrefix);
			return s;
		}
	}
	
	Pool<Node<T>> nodePool = new Pool<Node<T>>(){
		@Override
		protected Node<T> newObject() {
			return new Node<T>();
		}
	};
	
	
	public QuadTree() {
		this(1,1);
	}
	public QuadTree(Vector2 minSize) {
		this(minSize.x, minSize.y);
	}
	public QuadTree(float x, float y) {
		minSize.set(x, y);
	}
	
	public void add(T element, Rectangle bounds) {
		
		bx = (int)MathUtils.floor(bounds.x/minSize.x);
		by = (int)MathUtils.floor(bounds.y/minSize.y);
		bw = (int)MathUtils.ceil((bounds.x + bounds.width)/minSize.x) - bx;
		bh = (int)MathUtils.ceil((bounds.y + bounds.height)/minSize.y) - by;
		
		// just make a leaf node
		if(root == null){
			root = nodePool.obtain();
			ix = bx;
			iy = by;
			iw = 1;
			ih = 1;
		}
		
		// check if root is null, or is out of bound of root
		while(!contains(bx,by,bw,bh)) {
			
			// left/right overflow 
			boolean extendsBeforeX = bx < ix;
			boolean extendsBeforeY = by < iy;
			ix = extendsBeforeX ? ix-iw : ix;
			iy = extendsBeforeY ? iy-ih : iy;
			iw <<= 1;
			ih <<= 1;
			
			Node<T> newRoot = nodePool.obtain();
			newRoot.elements.addAll(root.elements); // recopy elements
			
			if(extendsBeforeX){
				if(extendsBeforeY)
					newRoot.n11 = root;
				else
					newRoot.n10 = root;
			}else{
				if(extendsBeforeY)
					newRoot.n01 = root;
				else
					newRoot.n00 = root;
			}
			
			root = newRoot;
		}
		
		add(root, element, ix, iy, iw, ih);
	}
	
	public void remove(T element){
		remove(root, element);
		if(root.elements.size == 0){
			nodePool.free(root); root = null;
			ix = iy = iw = ih = 0;
		}
	}
	private void remove(Node<T> node, T element){
		if(node != null){
			if(node.elements.removeValue(element, true)){
				remove(node.n00, element);
				remove(node.n10, element);
				remove(node.n01, element);
				remove(node.n11, element);
				if(node.n00 != null && node.n00.elements.size == 0){
					nodePool.free(node.n00); node.n00 = null;
				}
				if(node.n10 != null && node.n10.elements.size == 0){
					nodePool.free(node.n10); node.n10 = null;
				}
				if(node.n01 != null && node.n01.elements.size == 0){
					nodePool.free(node.n01); node.n01 = null;
				}
				if(node.n11 != null && node.n11.elements.size == 0){
					nodePool.free(node.n11); node.n11 = null;
				}
			}
		}
	}
	
	private boolean contains(int floorX, int floorY, int floorW, int floorH) {
		return floorX >= ix && floorX+floorW<=ix+iw && floorY >= iy && floorY+floorH<=iy+ih;
	}
	private void add(Node<T> node, T element, int nx, int ny, int nw, int nh) {
		
		node.elements.add(element);
		
		int hw = nw>>1;
		int hh = nh>>1;
			
		// leaf
		if(hw<=0||hh<=0) return;
		
		// subdivide
		int mx = nx+hw;
		int my = ny+hh;
		
		if(bx<mx && by<my) {
			if(node.n00 == null) node.n00 = nodePool.obtain();
			add(node.n00, element, nx, ny, hw, hh);
		}
		if(bx+bw>mx && by<my){
			if(node.n10 == null) node.n10 = nodePool.obtain();
			add(node.n10, element, mx, ny, hw, hh);
		}
		if(bx<mx && by+bh>my){
			if(node.n01 == null) node.n01 = nodePool.obtain();
			add(node.n01, element, nx, my, hw, hh);
		}
		if(bx+bw>mx && by+bh>my){
			if(node.n11 == null) node.n11 = nodePool.obtain();
			add(node.n11, element, mx, my, hw, hh);
		}
	}
	
	public Array<T> getElements(Rectangle bounds){
		result.clear();
		
		bx = (int)MathUtils.floor(bounds.x/minSize.x);
		by = (int)MathUtils.floor(bounds.y/minSize.y);
		bw = (int)MathUtils.ceil((bounds.x + bounds.width)/minSize.x) - bx;
		bh = (int)MathUtils.ceil((bounds.y + bounds.height)/minSize.y) - by;
		
		// overlap ?
		boolean boundsOutside = bx+bw <= ix || bx >= ix+iw || by+bh <= iy || by>= iy+ih;
		if(!boundsOutside){
			
			getElements(root, ix, iy, iw, ih);
		}
		return result;
	}
	private void getElements(Node<T> node, int nx, int ny, int nw, int nh) {
		
		// completely contained in requested boundray
		boolean insideBounds = bx <= nx && bx+bw>= nx+nw && by<=ny && by+bh>=ny+nh;
		if(insideBounds) {
			result.addAll(node.elements);
			return;
		}
		int hw = nw>>1;
		int hh = nh>>1;
		// too small
		if(hw<=0||hh<=0){
			result.addAll(node.elements);
			return;
		}
		int mx = nx+hw;
		int my = ny+hh;
		
		if(node.n00 != null && bx < mx && by < my) getElements(node.n00, nx, ny, hw, hh);
		if(node.n10 != null && bx+bw > mx && by < my) getElements(node.n10, mx, ny, hw, hh);
		if(node.n01 != null && bx < mx && by+bh > my) getElements(node.n01, nx, my, hw, hh);
		if(node.n11 != null && bx+bw > mx && by+bh > my) getElements(node.n11, mx, my, hw, hh);
	}
	
}
