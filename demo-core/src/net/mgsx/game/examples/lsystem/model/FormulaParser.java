package net.mgsx.game.examples.lsystem.model;

public class FormulaParser {

	public Formula parse(LSystem system, String formula)
	{
		String numeric = null;
		String text = null;
		Symbol current = null;
		Formula f = new Formula();
		f.value = formula;
		for(int i=0 ; i<formula.length() ; i++){
			
			char c = formula.charAt(i);
			if(numeric == null && text == null && system.hasSymbol(String.valueOf(c)))
			{
				current = system.symbol(String.valueOf(c));
				f.symbols.add(current);
			}
			else if(c >= '0' && c <= '9' || c == '.'  || c == '-')
			{
				numeric += String.valueOf(c);
			}
			else if(c == ',')
			{
				if(!text.isEmpty()){
					Parameter param = f.parameters.get(text);
					if(param == null) f.parameters.put(text, new Parameter(text));
					current.parameters.add(param);
				}
				else
					current.parameters.add(new Parameter(Float.valueOf(numeric)));
				numeric = "";
				text = "";
			}
			else if(c == '(')
			{
				text = "";
				numeric = "";
			}
			else if(c == ')')
			{
				if(!text.isEmpty()){
					Parameter param = f.parameters.get(text);
					if(param == null) f.parameters.put(text, param = new Parameter(text));
					current.parameters.add(param);
				}
				else
					current.parameters.add(new Parameter(Float.valueOf(numeric)));
				numeric = null;
				text = null;
			}
			else
			{
				text += String.valueOf(c);
			}
			
			
		}
		
		
		return f;
	}
}
