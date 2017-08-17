package net.mgsx.game.examples.lsystem.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import net.mgsx.game.examples.lsystem.model.Formula;
import net.mgsx.game.examples.lsystem.model.FormulaParser;
import net.mgsx.game.examples.lsystem.model.LSystem;
import net.mgsx.game.examples.lsystem.model.Parameter;
import net.mgsx.game.examples.lsystem.model.Rule;
import net.mgsx.game.examples.lsystem.model.l2d.Rules2D;

public class FormulaParserTest {

	private LSystem system;
	
	@Before
	public void setup(){
		system = new LSystem();
		system.rule("+", Rules2D.rotateLeft);
		system.rule("-", Rules2D.rotateRight);
		system.rule("O", Rules2D.circle);
		system.rule("F", Rules2D.line);
		system.rule("S", Rules2D.size);
		system.rule("[", Rule.push);
		system.rule("]", Rule.pop);
	}
	
	@Test
	public void testNominal(){
		Formula f = new FormulaParser().parse(system, "F[+F]F[-F]F");
		assertEquals(11, f.symbols.size);
		assertEquals(Rules2D.line, f.symbols.get(0).defaultRule);
		
		assertEquals(Rule.push, f.symbols.get(1).defaultRule);
		assertEquals(Rules2D.rotateLeft, f.symbols.get(2).defaultRule);
		assertEquals(Rules2D.line, f.symbols.get(3).defaultRule);
		assertEquals(Rule.pop, f.symbols.get(4).defaultRule);
		
		assertEquals(Rules2D.line, f.symbols.get(5).defaultRule);
		
		assertEquals(Rule.push, f.symbols.get(6).defaultRule);
		assertEquals(Rules2D.rotateRight, f.symbols.get(7).defaultRule);
		assertEquals(Rules2D.line, f.symbols.get(8).defaultRule);
		assertEquals(Rule.pop, f.symbols.get(9).defaultRule);
		
		assertEquals(Rules2D.line, f.symbols.get(10).defaultRule);
	}
	
	@Test
	public void testParameters()
	{
		Formula f = new FormulaParser().parse(system, "S(123,1.56,-5.4,size)S");
		assertEquals(2, f.symbols.size);
		assertEquals(Rules2D.size, f.symbols.get(0).defaultRule);
		assertEquals(Rules2D.size, f.symbols.get(1).defaultRule);
		assertEquals(4, f.symbols.get(0).parameters.size);
		assertEquals(123, f.symbols.get(0).parameters.get(0).value, Float.MIN_VALUE);
		assertEquals(1.56f, f.symbols.get(0).parameters.get(1).value, Float.MIN_VALUE);
		assertEquals(-5.4f, f.symbols.get(0).parameters.get(2).value, Float.MIN_VALUE);
		assertEquals(f.parameters.get("size"), f.symbols.get(0).parameters.get(3));
	}
	@Test
	public void testParameterConstant()
	{
		Formula f = new FormulaParser().parse(system, "S(30)");
		assertEquals(1, f.symbols.size);
		assertEquals(Rules2D.size, f.symbols.get(0).defaultRule);
		assertEquals(1, f.symbols.get(0).parameters.size);
		assertEquals(30, f.symbols.get(0).parameters.get(0).value, Float.MIN_VALUE);
	}
	@Test
	public void testParameterVar()
	{
		Formula f = new FormulaParser().parse(system, "S(size)");
		assertEquals(1, f.symbols.size);
		assertEquals(Rules2D.size, f.symbols.get(0).defaultRule);
		assertEquals(1, f.symbols.get(0).parameters.size);
		assertEquals(1, f.parameters.size);
		Parameter param = f.parameters.get("size");
		assertNotNull(param);
		assertEquals(f.parameters.get("size"), f.symbols.get(0).parameters.get(0));
		assertEquals("size", f.parameters.get("size").name);
	}
	
}
