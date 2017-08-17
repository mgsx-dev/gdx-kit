package net.mgsx.game.examples.procedural.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class LSystem 
{
	ObjectMap<String, Rule> rules = new ObjectMap<String, Rule>();
	public ObjectMap<String, Formula> substitutions = new ObjectMap<String, Formula>();
	private ObjectMap<String, Parameter> parameters = new ObjectMap<String, Parameter>();
	
	/**
	 * Retreive or create global parameter
	 * @param key
	 * @return
	 */
	public Parameter parameter(String key) {
		Parameter p = parameters.get(key);
		if(p == null) parameters.put(key, p = new Parameter(key));
		return p;
	}
	
	public Array<Parameter> parameters(){
		return parameters.values().toArray();
	}
	
	public Formula substitution(String symbol, Object...symbols){
		Formula f = new Formula();
		for(Object s : symbols){
			if(s instanceof Symbol)
				f.symbols.add((Symbol)s);
			else if(s instanceof String)
				f.symbols.add(symbol((String)s));
		}
		substitutions.put(symbol, f);
		f.symbol = symbol;
		return f;
	} 
	public Formula substitution(String symbol){
		return substitutions.get(symbol);
	}
	
	public Symbol symbol(String symbol){
		return new Symbol(symbol, rules.get(symbol), substitutions.get(symbol));
	}
	public Symbol symbol(String symbol, float...parameters) {
		Symbol s = symbol(symbol);
		for(float p : parameters) s.parameters.add(new Parameter(p));
		return s;
	}
	
	public boolean hasSymbol(String symbol){
		return rules.containsKey(symbol) || substitutions.containsKey(symbol);
	}

	public void rule(String symbol, Rule rule){
		rules.put(symbol, rule);
	}

	public Array<Formula> substitutions() {
		return substitutions.values().toArray();
	}

	public void update() 
	{
		for(Entry<String, Formula> entry : substitutions.entries()){
			entry.value.set(new FormulaParser().parse(this, entry.value.value));
		}
		
	}

	
}
