package net.mgsx.game.core.helpers;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.graphics.Color;

public class ColorHelperTest {

	private final static float epsilon = 1e-4f;
	
	@Test
	public void testToHSV(){
		float [] hsv = {0,0,0};
		Color c = new Color(1,0,0,1);
		ColorHelper.colorToHsv(hsv, c);
		Assert.assertEquals(0, hsv[0], epsilon);
	}
	@Test
	public void testFromHSV(){
		float [] hsv = {360,1,1};
		Color c = ColorHelper.hsvToColor(new Color(Color.BLACK), hsv);
		Assert.assertEquals(1, c.r, epsilon);
		Assert.assertEquals(0, c.g, epsilon);
		Assert.assertEquals(0, c.b, epsilon);
		Assert.assertEquals(1, c.a, epsilon);
	}
	@Test
	public void testExtremeConversions(){
		float [] hsv = {0,0,0};
		Color c = new Color(0,0,0,1);
		c.r = 4;
		c.g = 2;
		c.b = 1;
		ColorHelper.colorToHsv(hsv, c);
//		Assert.assertEquals(30, hsv[0], epsilon);
//		Assert.assertEquals(1, hsv[1], epsilon);
//		Assert.assertEquals(2, hsv[2], epsilon);
		Color res = ColorHelper.hsvToColor(new Color(Color.BLACK), hsv);
		
		Assert.assertEquals(4, res.r, epsilon);
		Assert.assertEquals(2, res.g, epsilon);
		Assert.assertEquals(1, res.b, epsilon);
		Assert.assertEquals(1, res.a, epsilon);
		
	}
}
