package net.mgsx.game.examples.procedural.model;

import com.badlogic.gdx.utils.Array;

public class Symbol {
	String id;
	Formula substitution;
	Rule defaultRule;
	public Array<Parameter> parameters = new Array<Parameter>();
	public Symbol next;
	Symbol(String id, Rule defaultRule, Formula substitution) {
		super();
		this.id = id;
		this.defaultRule = defaultRule;
		this.substitution = substitution;
	}
}
