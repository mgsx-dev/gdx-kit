package net.mgsx.game.core.math;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class QuadTreeTest {

	@Test
	public void test(){
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add("square 1", new Rectangle(3, 3, 2, 2));
		
		Array<String> r = qt.getElements(new Rectangle(0, 0, 10, 10));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square 1", r.first());
	}
	@Test
	public void test_getElements_smallObject(){
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add("square", new Rectangle(3.5f, 3.5f, .2f, .2f));
		
		Array<String> r;
		
		r = qt.getElements(new Rectangle(0, 0, 10, 10));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square", r.first());
		
		r = qt.getElements(new Rectangle(3, 3, 1, 1));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square", r.first());
		
		// out rectangle in cell return the object anyway
		r = qt.getElements(new Rectangle(3.3f, 3.3f, .1f, .1f));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square", r.first());
		
		r = qt.getElements(new Rectangle(2, 3, 1, 1));
		Assert.assertEquals(0, r.size);
		
		r = qt.getElements(new Rectangle(4, 3, 1, 1));
		Assert.assertEquals(0, r.size);
		
		r = qt.getElements(new Rectangle(-100, -100, 103, 103));
		Assert.assertEquals(0, r.size);
	}
	
	@Test
	public void test_getElements_bigObject(){
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add("square", new Rectangle(3.5f, 3.5f, 3, 3)); // 3 3 to 7 7
		
		Array<String> r;
		
		r = qt.getElements(new Rectangle(0, 0, 10, 10));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square", r.first());
		
		r = qt.getElements(new Rectangle(3, 3, 1, 1));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square", r.first());
		
		r = qt.getElements(new Rectangle(5, 5, 100, 100));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square", r.first());

		r = qt.getElements(new Rectangle(2, 3, 1, 1));
		Assert.assertEquals(0, r.size);
		
		r = qt.getElements(new Rectangle(7, 7, 1, 1));
		Assert.assertEquals(0, r.size);
		
		r = qt.getElements(new Rectangle(-100, -100, 103, 103));
		Assert.assertEquals(0, r.size);
	}
	
	@Test
	public void testBase(){
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add("square 1", new Rectangle(0, 0, 1, 1));
		
		Array<String> r = qt.getElements(new Rectangle(0, 0, 1, 1));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square 1", r.first());
	}
	@Test
	public void testFarGrid(){
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add("square 1", new Rectangle(100, 100, 1, 1));
		
		Array<String> r = qt.getElements(new Rectangle(0, 0, 200, 200));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square 1", r.first());
	}
	@Test
	public void testSeparatedObjects(){
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add("square 1", new Rectangle(-100, -100, 1, 1));
		qt.add("square 2", new Rectangle(100, 100, 1, 1));
		
		Array<String> r = qt.getElements(new Rectangle(-200, -200, 400, 400));
		Assert.assertEquals(2, r.size);
		Assert.assertEquals("square 1", r.get(0));
		Assert.assertEquals("square 2", r.get(1));
	}
	
	@Test
	public void testEmptyResult(){
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add("square 1", new Rectangle(-100, -100, 1, 1));
		qt.add("square 2", new Rectangle(100, 100, 1, 1));
		
		Array<String> r = qt.getElements(new Rectangle(-99, -99, 198, 198));
		Assert.assertEquals(0, r.size);
	}
	@Test
	public void testEmptyRequestMachOne(){
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add("square 1", new Rectangle(-100, -100, 1, 1));
		qt.add("square 2", new Rectangle(100, 100, 1, 1));
		
		Array<String> r = qt.getElements(new Rectangle(100, 100, 1, 1));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square 2", r.get(0));
	}
	@Test
	public void testRemove(){
		String s1 = "square 1";
		String s2 = "square 2";
		QuadTree<String> qt = new QuadTree<String>(1, 1);
		qt.add(s1, new Rectangle(-100, -100, 1, 1));
		qt.add(s2, new Rectangle(100, 100, 1, 1));

		Array<String> r;
		
		r = qt.getElements(new Rectangle(-101, -101, 2, 2));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square 1", r.get(0));
		
		r = qt.getElements(new Rectangle(99, 99, 2, 2));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square 2", r.get(0));

		qt.remove(s1);
		
		r = qt.getElements(new Rectangle(-101, -101, 2, 2));
		Assert.assertEquals(0, r.size);
		
		r = qt.getElements(new Rectangle(99, 99, 2, 2));
		Assert.assertEquals(1, r.size);
		Assert.assertEquals("square 2", r.get(0));
	}
	
	

}
