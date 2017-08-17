package net.mgsx.game.examples.lsystem.model;

public interface Rule<T>
{
	public void init(Generator<T> context);
	public void execute(int depth);
	
	public static class RuleBase<T> implements Rule<T>{
		protected Generator<T> context;
		@Override
		public void init(Generator<T> context) {
			this.context = context;
		}
		@Override
		public void execute(int depth) {
		}
	}
	
	public static final Rule push = new RuleBase<Context>() {
		@Override
		public void execute(int depth) {
			// copy context (pos/rot/scale)
			// push to stack
			Context c = context.current;
			Context o = context.pool.obtain();
			c.clone(o);
			context.stack.add(context.current);
			context.current = o;
		}
	};
	public static final Rule pop = new RuleBase<Context>() {
		@Override
		public void execute(int depth) {
			// pop stack (make current)
			context.pool.free(context.current);
			context.current = context.stack.pop();
		}
	};
	public static final Rule loopStart = new RuleBase<Context>() {
		@Override
		public void execute(int depth) {
			int count = (int)context.symbol.parameters.get(0).value;
			Loop loop = context.loopPool.obtain();
			loop.count = count;
			loop.jump = context.symbol;
			context.loops.add(loop);
		}
	};
	public static final Rule loopEnd = new RuleBase<Context>() {
		@Override
		public void execute(int depth) {
			Loop loop = context.loops.peek();
			if(--loop.count <= 0){
				context.loopPool.free(context.loops.pop());
			}else{
				context.symbol = loop.jump;
			}
		}
	};
}
