package net.mgsx.game.examples.lsystem.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.lsystem.model.Context;
import net.mgsx.game.examples.lsystem.model.Formula;
import net.mgsx.game.examples.lsystem.model.FormulaParser;
import net.mgsx.game.examples.lsystem.model.Generator;
import net.mgsx.game.examples.lsystem.model.LSystem;
import net.mgsx.game.examples.lsystem.model.Rule;
import net.mgsx.game.examples.lsystem.model.Rule.RuleBase;

public class LSystemTest {

	public static class ContextTest implements Context<ContextTest>{
		public int p;

		@Override
		public void clone(ContextTest clone) {
			clone.p = p;
		}
	}
	private LSystem system;
	private Array<Integer> results;
	
	@Before
	public void setup()
	{
		results = new Array<Integer>();
		
		system = new LSystem();
		
		system.rule("[", Rule.push);
		system.rule("]", Rule.pop);
		system.rule("{", Rule.loopStart);
		system.rule("}", Rule.loopEnd);
		
		system.rule("F", new RuleBase<ContextTest>(){
			@Override
			public void execute(int depth) {
				results.add(context.current.p);
				context.current.p++;
			}
		});
	}
	
	private Formula formula(String formula) {
		return new FormulaParser().parse(system, formula);
	}
	
	public void generate(String symbol, int depth){
		Generator<ContextTest> gen = new Generator<ContextTest>(ContextTest.class, system);
		results.clear();
		gen.generate(system.symbol(symbol), depth);
	}
	
	public void verify(Integer...positions)
	{
		assertEquals(new Array<Integer>(positions), results);
	}
	
	@Test
	public void testStack()
	{
		// configure system
		system.substitutions.put("F", formula("F[FF]F"));
		// generate
		generate("F", 1);
		// check
		verify(0,1,2,1);
	}

	@Test
	public void testLoops()
	{
		// configure system
		system.substitutions.put("F", formula("F{(3)FF}F"));
		// generate
		generate("F", 1);
		// check
		verify(0,1,2,3,4,5,6,7);
	}
	
}
