package net.mgsx.game.examples.lsystem.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Formula {
	public String symbol;
	public String value;
	public Array<Symbol> symbols = new Array<Symbol>();
	public ObjectMap<String, Parameter> parameters = new ObjectMap<String, Parameter>();
	public void set(Formula f) {
		symbol = f.symbol;
		value = f.value;
		symbols = f.symbols;
		parameters = f.parameters;
	}
}
