package net.mgsx.game.examples.openworld.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OpenWorldPathBuilderTest {

	private static final float epsilon = .1f;

	OpenWorldPathBuilder builder;
	
	@Before
	public void setup(){
		builder = new OpenWorldPathBuilder();
		builder.set(null, 30, 5, 0); // no randomness
		builder.resetLimit();
	}
	
	@Test
	public void testComputeHeight_default(){
		Assert.assertEquals(50, builder.computeHeight(50), epsilon);
	}
	
	@Test
	public void testComputeHeight_allLimits(){
		
		// configure all limits :
		// =============== GND + 6m
		//
		// allowed altitude
		//
		// =============== GND + 4m
		
		// --------------- 100m
		//
		// allowed altitude
		//
		// --------------- 0m
		
		builder
		.groundMin(4)
		.groundMax(6)
		.absoluteMin(0)
		.absoluteMax(100);
		
		
		// GND = 0
		// => 4 to 6 && 0 to 100 => 4 to 6 => 5
		Assert.assertEquals("ground limits within abs limits",
				5, builder.computeHeight(0), epsilon);
		
		// over abs max
		Assert.assertEquals("ground above absolute max", 
				100, builder.computeHeight(200), epsilon);
		
		// GND = -200
		Assert.assertEquals("ground below absolute min",
				0, builder.computeHeight(-200), epsilon);
		
		Assert.assertEquals("ground min above abs max",
				100, builder.computeHeight(99), epsilon);
		
		// 99 to 100
		Assert.assertEquals("ground max above abs max",
				99.5f, builder.computeHeight(95), epsilon);
		
		Assert.assertEquals("ground max below abs min",
				0, builder.computeHeight(-7), epsilon);
		
		// -1 to 1 clipped to 0 to 1
		Assert.assertEquals("ground min below abs min",
				0.5f, builder.computeHeight(-5), epsilon);
		

	}

}
