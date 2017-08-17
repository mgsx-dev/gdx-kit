package net.mgsx.game.examples.lsystem.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

import net.mgsx.game.examples.lsystem.model.Context;
import net.mgsx.game.examples.lsystem.model.Formula;
import net.mgsx.game.examples.lsystem.model.Generator;
import net.mgsx.game.examples.lsystem.model.LSystem;
import net.mgsx.game.examples.lsystem.model.Rule;
import net.mgsx.game.examples.lsystem.model.Symbol;
import net.mgsx.game.examples.lsystem.model.Rule.RuleBase;

//TODO use a small symbol set : F(store current position) +(increment position)
//

public class GeneratorTest {

	@Before
	public void setup(){
		
	}
	
	public static class ContextTest implements Context<ContextTest>
	{
		int pos;
		@Override
		public void clone(ContextTest clone) {
			clone.pos = pos;
		}
	}
	
	@Test
	public void testGenerateRecursive()
	{
		Formula f = new Formula();
		
		final Array<Integer> r = new Array<Integer>();
		
		LSystem system = new LSystem();
		system.rule("F", new RuleBase<ContextTest>() {
			@Override
			public void execute(int depth) {
				r.add(depth);
			}
		});
		system.substitution("F", "F", "F");
		
		Generator<ContextTest> gen = new Generator<ContextTest>(ContextTest.class, system);
		
		Symbol F = system.symbol("F");
		
		// depth 0 => F
		r.clear();
		gen.generate(F , 0);
		assertEquals(1, r.size);
		
		// depth 1 => F F
		r.clear();
		gen.generate(F, 1);
		assertEquals(2, r.size);

		// depth 2 => F F F F
		r.clear();
		gen.generate(F, 2);
		assertEquals(4, r.size);

		// depth 3 => F F F F F F F F
		r.clear();
		gen.generate(F, 3);
		assertEquals(8, r.size);
	}
	
	@Test
	public void testGenerateStack()
	{
		final Array<Integer> r = new Array<Integer>();
		
		LSystem system = new LSystem();
		
		// define rules
		system.rule("[", Rule.push);
		system.rule("]", Rule.pop);
		system.rule("F", new RuleBase<ContextTest>() {
			@Override
			public void execute(int depth) {
				r.add(context.current.pos);
			}
		});
		system.rule("m", new RuleBase<ContextTest>() {
			@Override
			public void execute(int depth) {
				context.current.pos += 1;
			}
		});
		
		
		// define substitution
		system.substitution("F", "F", "[", "m", "F", "]", "F");
		
		system.parameter("a").value = 30;
		
		Generator<ContextTest> gen = new Generator<ContextTest>(ContextTest.class, system);
		
		Symbol F = system.symbol("F");
		
		// depth 0 => F m F F
		r.clear();
		gen.generate(F, 1);
		assertEquals(3, r.size);
		int i=0;
		assertEquals(0, (int)r.get(i++));
		assertEquals(1, (int)r.get(i++));
		assertEquals(0, (int)r.get(i++));
		
	}
	
	@Test
	public void testGenerateLoops()
	{
		final Array<Integer> r = new Array<Integer>();
		
		LSystem system = new LSystem();
		
		// define rules
		system.rule("{", Rule.loopStart);
		system.rule("}", Rule.loopEnd);
		system.rule("F", new RuleBase<ContextTest>() {
			@Override
			public void execute(int depth) {
				r.add(context.current.pos);
			}
		});
		system.rule("m", new RuleBase<ContextTest>() {
			@Override
			public void execute(int depth) {
				context.current.pos += 1;
			}
		});
		
		
		// define substitution
		system.substitution("F", "F", system.symbol("{", 3), "m", "F", "}", "F");
		
		Generator<ContextTest> gen = new Generator<ContextTest>(ContextTest.class, system);
		
		Symbol F = system.symbol("F");
		
		// depth 0 => F m F F
		r.clear();
		gen.generate(F, 1);
		assertEquals(5, r.size);
		int i=0;
		assertEquals(0, (int)r.get(i++));
		assertEquals(1, (int)r.get(i++));
		assertEquals(2, (int)r.get(i++));
		assertEquals(3, (int)r.get(i++));
		assertEquals(3, (int)r.get(i++));
		
	}
	

	@Test
	public void testGenerateNestedLoopsOld()
	{
		final Array<Integer> r = new Array<Integer>();
		
		LSystem system = new LSystem();
		
		// define rules
		system.rule("{", Rule.loopStart);
		system.rule("}", Rule.loopEnd);
		system.rule("F", new RuleBase<ContextTest>() {
			@Override
			public void execute(int depth) {
				r.add(context.current.pos);
			}
		});
		system.rule("m", new RuleBase<ContextTest>() {
			@Override
			public void execute(int depth) {
				context.current.pos += 1;
			}
		});
		
		
		// define substitution
		system.substitution("F", "F", system.symbol("{", 3), "m", system.symbol("{", 2), "m", "F", "}", "m", "}", "F");
		
		Generator<ContextTest> gen = new Generator<ContextTest>(ContextTest.class, system);
		
		Symbol F = system.symbol("F");
		
		// depth 0 => F (m(mF mF)m) (m(mF mF)m) (m(mF mF)m) F
		// => 0 2 3 6 7 10 11 12
		r.clear();
		gen.generate(F, 1);
		assertEquals(8, r.size);
		int i=0;
		assertEquals(0, (int)r.get(i++));
		assertEquals(2, (int)r.get(i++));
		assertEquals(3, (int)r.get(i++));
		assertEquals(6, (int)r.get(i++));
		assertEquals(7, (int)r.get(i++));
		assertEquals(10, (int)r.get(i++));
		assertEquals(11, (int)r.get(i++));
		assertEquals(12, (int)r.get(i++));
		
	}
	
	
	
}
