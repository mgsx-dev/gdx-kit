package net.mgsx.game.core.helpers;

import org.junit.Assert;
import org.junit.Test;

public class StringHelperTest {

	@Test
	public void testCamelCaseToUnderScoreUpperCase_nominal(){
		Assert.assertEquals(
				"THIS_IS_STANDARD_CAMEL_CASE_STRING", StringHelper.camelCaseToUnderScoreUpperCase(
				"thisIsStandardCamelCaseString"));
	}
	@Test
	public void testCamelCaseToUnderScoreUpperCase_upperCase(){
		Assert.assertEquals(
				"notCAMELCase", StringHelper.camelCaseToUnderScoreUpperCase(
				"notCAMELCase"));
	}
	@Test
	public void testCamelCaseToUnderScoreUpperCase_Underscored(){
		Assert.assertEquals(
				"not_a_camel_case", StringHelper.camelCaseToUnderScoreUpperCase(
				"not_a_camel_case"));
	}
	@Test
	public void testCamelCaseToUnderScoreUpperCase_empty(){
		Assert.assertEquals(
				"", StringHelper.camelCaseToUnderScoreUpperCase(
				""));
	}
	@Test
	public void testCamelCaseToUnderScoreUpperCase_digits(){
		Assert.assertEquals(
				"notCamelCase0", StringHelper.camelCaseToUnderScoreUpperCase(
				"notCamelCase0"));
	}
}
