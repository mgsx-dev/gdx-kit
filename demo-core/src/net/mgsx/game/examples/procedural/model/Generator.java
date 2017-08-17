package net.mgsx.game.examples.procedural.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Pool;

import net.mgsx.game.core.helpers.ReflectionHelper;

public class Generator<T>
{
	Pool<T> pool;
	public Array<T> stack = new Array<T>();
	
	public T current;
	public LSystem system;
	public Symbol symbol;
	public Array<Loop> loops = new Array<Loop>();
	
	public Pool<Loop> loopPool = new Pool<Loop>(){
		@Override
		protected Loop newObject() {
			return new Loop();
		}};
	
	public Generator(final Class<T> type, LSystem system){
		super();
		this.system = system;
		pool = new Pool<T>(){
			@Override
			protected T newObject() {
				return ReflectionHelper.newInstance(type);
			}
		};
	}
	
	public void generateAtDepth(Symbol symbol, int depth){
		
	}
	
	public void begin(){
		if(current == null)
			current = pool.obtain();
	}
	
	public void generate(Symbol symbol, int depth)
	{
		// pre process symbol
		for(Entry<String, Formula> entry : system.substitutions.entries()){
			Symbol prev = null;
			for(Symbol s : entry.value.symbols){
				s.defaultRule.init(this);
				s.substitution = system.substitutions.get(s.id);
				if(prev != null) prev.next = s;
				prev = s;
			}
		}
		
		loops.clear();
		
		begin();
		
		this.symbol = symbol;
		generateRecursive(depth);
		pool.free(current); 
		current = null;
	}
	
	private void generateRecursive(int depth){
		
		if(depth > 0 && symbol.substitution != null){
			Symbol next = symbol.next;
			// symbol.defaultRule.execute(depth);
			
			symbol = symbol.substitution.symbols.first();
			while(this.symbol != null){
				generateRecursive(depth-1);
			}
			symbol = next;
			
//			for(int i=0 ; i<symbol.substitution.symbols.size ; i++){
//				Symbol s = symbol.substitution.symbols.get(i);
//				generateRecursive(s, depth-1);
//			}
		}else{
			symbol.defaultRule.execute(depth);
			symbol = symbol.next;
		}
	}

}
