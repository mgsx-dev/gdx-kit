package net.mgsx.game.examples.openworld.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

/**
 * Test have to be maintain, use TestGUI instead !
 * 
 * @author mgsx
 *
 */
public class VirtualGridTest {

	protected static class VirtualGridString extends VirtualGrid<String>
	{
		public Array<String> created =  new Array<String>();
		public Array<String> entered =  new Array<String>();
		public Array<String> exited  =  new Array<String>();
		public Array<String> removed =  new Array<String>();
		
		
		@Override
		protected void dispose(String cell) {
			removed.add(cell);
		}
		
		@Override
		protected String create(float worldX, float worldY) {
			String object = String.format("%.0f,%.0f", worldX, worldY);
			created.add(object);
			return object;
		}

		@Override
		protected void enter(String cell) {
			entered.add(cell);
		}

		@Override
		protected void exit(String cell) {
			exited.add(cell);
		}

		public void resetTest() {
			created.clear();
			entered.clear();
			exited.clear();
			removed.clear();
		}

		public void assertAllEmpty() {
			assertSame(created);
			assertSame(entered);
			assertSame(exited);
			assertSame(removed);
		}

		@Override
		protected void update(String cell, int distance) {
		}
	}
	
	protected VirtualGridString grid;
	
	protected static void assertSame(Array<String> actual, String...expected){
		Array<String> expectedArray = new Array<String>(expected);
		Array<String> actualMissing = new Array<String>();
		Array<String> expectedMissing = new Array<String>();
		
		for(String exp : expected){
			if(!actual.contains(exp, false)){
				actualMissing.add(exp);
			}
		}
		for(String act : actual){
			if(!expectedArray.contains(act, false)){
				expectedMissing.add(act);
			}
		}
		String error = "";
		if(actualMissing.size > 0){
			error += "\nactual missing :";
			for(String s : actualMissing) error += " [" + s + "]";
		}
		if(expectedMissing.size > 0){
			error += "\nactual unexpected :";
			for(String s : expectedMissing) error += " [" + s + "]";
		}
		if(error.length() > 0){
			error += "\nactual was :";
			for(String s : actual) error += " [" + s + "]";
			error += "\nexpected was :";
			for(String s : expected) error += " [" + s + "]";
			Assert.fail(error);
		}
	}
	
	@Before
	public void setup(){
		grid = new VirtualGridString();
	}
	
	@Ignore
	@Test
	public void test_innerOdd()
	{
		grid.resize(2, 2, 0, 0, 10f);
		
		grid.update(3f, 12f); // rounded to to 0,10
		
		assertSame(grid.removed);
		
		assertSame(grid.created, 
				"-10,0",  "0,0",
				"-10,10", "0,10");
		
		assertSame(grid.entered, 
				"-10,0",  "0,0",
				"-10,10", "0,10");
		
		assertSame(grid.removed);
		assertSame(grid.exited);
	}
	
	@Ignore
	@Test
	public void test_innerEven()
	{
		grid.resize(3, 3, 0, 0, 10f);
		
		grid.update(3f, 12f); // to 0,10
		
		
		assertSame(grid.created, 
				"-10,0",  "0,0", "10,0",
				"-10,10", "0,10", "10,10", 
				"-10,20", "0,20", "10,20");
		
		assertSame(grid.entered, 
				"-10,0",  "0,0", "10,0",
				"-10,10", "0,10", "10,10", 
				"-10,20", "0,20", "10,20");
		
		assertSame(grid.removed);
		assertSame(grid.exited);
	}
	
	@Ignore
	@Test
	public void test_smallest()
	{
		grid.resize(1, 1, 0, 0, 10f);
		grid.update(3f, 12f);
		
		assertSame(grid.created, "0,10");
		assertSame(grid.entered, "0,10");
		assertSame(grid.removed);
		assertSame(grid.exited);
	}
	
	@Ignore
	@Test
	public void test_nominal()
	{
		// -----
		// -----
		// -xxx-
		// -xxx-
		// -xxx-
		// -xxx-
		// -----
		// -----
		// 
		grid.resize(3, 4, 1, 2, 10f);
		
		// start in cell [40--, -20++]
		grid.update(41f, -19f);
		
		assertSame(grid.created,
				"30,-10", "40,-10", "50,-10",
				"30,-20", "40,-20", "50,-20",
				"30,-30", "40,-30", "50,-30",
				"30,-40", "40,-40", "50,-40"
				);
		
		// same as created
		assertSame(grid.created,
				"30,-10", "40,-10", "50,-10",
				"30,-20", "40,-20", "50,-20",
				"30,-30", "40,-30", "50,-30",
				"30,-40", "40,-40", "50,-40"
				);
		assertSame(grid.removed);
		assertSame(grid.exited);
		
		
		
		// move a little to not spaw new things
		grid.resetTest();
		grid.update(44.9f, -24.9f);
		grid.assertAllEmpty();
		
		grid.resetTest();
		grid.update(35.1f, -24.9f); // TODO not good
		grid.assertAllEmpty();
		
		// move to top and expect 3 creations and 3 exits
		grid.resetTest();
		grid.update(41f, -14.9f); // center at [40--, -10--]
		
		assertSame(grid.created,
				"30,0", "40,0", "50,0"
				);
		assertSame(grid.entered,
				"30,0", "40,0", "50,0"
				);
		assertSame(grid.exited,
				"30,-40", "40,-40", "50,-40"
				);
		assertSame(grid.removed);
		
		// now move enough to check removal
		grid.resetTest();
		grid.update(71f, -14.9f); // center at [70--, -10--]
		
		assertSame(grid.created,
				"60,0",   "70,0",   "80,0",
				"60,-10", "70,-10", "80,-10",
				"60,-20", "70,-20", "80,-20",
				"60,-30", "70,-30", "80,-30");
		
		assertSame(grid.entered,
				"60,0",   "70,0",   "80,0",
				"60,-10", "70,-10", "80,-10",
				"60,-20", "70,-20", "80,-20",
				"60,-30", "70,-30", "80,-30");
		
		assertSame(grid.exited,
				"50,0",
				"50,-10",
				"50,-20",
				"50,-30");
		
		assertSame(grid.removed,
				"40,0",
				"40,-10",
				"40,-20",
				"40,-30");
		
	}
}
