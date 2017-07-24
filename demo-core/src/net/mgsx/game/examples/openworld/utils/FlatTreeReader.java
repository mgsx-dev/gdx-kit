package net.mgsx.game.examples.openworld.utils;

import java.util.Scanner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.annotations.Incubation;

@Incubation
public class FlatTreeReader {

	public static class NodeValue{
		private static NodeValue MISSING = new NodeValue();
		String name;
		NodeValue parent;
		public Array<NodeValue> children = new Array<NodeValue>();
		public NodeValue child(String name) {
			for(NodeValue c : children){
				if(name.equals(c.name)) return c;
			}
			return MISSING;
		}
		public NodeValue child(int i) {
			if(i >= children.size) return MISSING;
			return children.get(i);
		}
		public String asString(){
			return name;
		}
		public float asFloat(){
			return Float.parseFloat(name);
		}
		public NodeValue property(String name) {
			return child(name).child(0);
		}
		public boolean has(String name) {
			return child(name) != MISSING;
		}
		public float asFloat(float def) {
			return this == MISSING ? def : asFloat();
		}
		public boolean exists() {
			return this != MISSING;
		}
		public NodeValue first() {
			return children.size > 0 ? children.first() : MISSING;
		}
		public int asInt() {
			return Integer.parseInt(name);
		}
		public int asInt(int def) {
			try{
				return this == MISSING ? def : asInt();
			}catch(NumberFormatException e){
				Gdx.app.error("FlatTree", "excpected integer, get " + String.valueOf(name));
				return def;
			}
		}
	}
	
	public NodeValue parse(FileHandle file){
		final Scanner s = new Scanner(file.read());
		int pIndent = 0;
		NodeValue current = new NodeValue();
		while(s.hasNextLine()) {
		    final String line = s.nextLine();
		    String[] t = line.split("[ ]{4}");
		    int indent = Math.max(0, t.length-1);
		    while(indent < pIndent){
		    	if(current != null) current = current.parent;
		    	pIndent--;
		    }
		    NodeValue child = new NodeValue();
		    if(indent > pIndent){
		    	if(current != null)current.children.add(child);
		    }else{
		    	current = current.parent;
		    	if(current != null) current.children.add(child);
		    }
		    child.parent = current;
		    current = child;
		    
		    current.name = t.length > 0 ? t[t.length-1] : "";
		    pIndent = indent;
		}
		if(current != null)
			while(current.parent != null) current = current.parent;
		s.close();
		return current;
	}
}
