package net.mgsx.game.examples.openworld.utils;


import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

//TODO pullup in KIT
public class FreeMindReader
{
	private static class FreemindPullParser extends XmlReader {
		
		private FreemindNode current;
		private FreemindMap map;
		
		@Override
		protected void open(String name) {
			if("map".equals(name)){
				map = new FreemindMap();
			}else{
				FreemindNode node = new FreemindNode();
				if(map.root == null) map.root = node;
				if(current != null) current.children.add(node);
				node.parent = current;
				current = node;
			}
		}
		@Override
		protected void close() {
			if(current != null) current = current.parent;
		}
		@Override
		protected void attribute(String name, String value) {
			if(current == null){
				if("version".equals(name)){
					map.version = value;
				}
			} else if("TEXT".equals(name)){
				// TODO workaround on issue https://github.com/libgdx/libgdx/issues/4831
				current.text = StringEscapeUtils.unescapeXml(value);
			}
		}
	}
	public static class FreemindMap {
		String version;
		FreemindNode root;
		public FreemindNode root() {
			return root;
		}
	}
	public static class FreemindNodeDefault extends FreemindNode {
		@Override
		public FreemindNode child(String name) {
			return this;
		}
		@Override
		public FreemindNode property(String name) {
			return this;
		}
		@Override
		public FreemindNode child(int i) {
			return this;
		}
		@Override
		public float asFloat(float defaultValue) {
			return defaultValue;
		}
		@Override
		public boolean exists() {
			return false;
		}
		@Override
		public String asString() {
			return null;
		}
		public String asString(String defaultValue) {
			return defaultValue;
		}
	}
	public static class FreemindNode {
		public static final FreemindNode DEFAULT = new FreemindNodeDefault();
		
		FreemindNode parent;
		Array<FreemindNode> children = new Array<FreemindNode>();
		String text;
		
		@Override
		public String toString() {
			return toString("");
		}
		public FreemindNode child(String name) {
			// TODO Auto-generated method stub
			for(FreemindNode c : children){
				if(name.equals(c.text)) return c;
			}
			return DEFAULT;
		}
		public String toString(String prefix) {
			String s = prefix + text;
			for(FreemindNode c : children){
				s += "\n" + c.toString(prefix + "    ");
			}
			return s;
		}
		public FreemindNode property(String name) {
			return child(name).child(0);
		}
		public FreemindNode child(int i) {
			return i<children.size ? children.get(i) : DEFAULT;
		}
		public float asFloat(float defaultValue) {
			try{
				return Float.parseFloat(text);
			}catch(NumberFormatException e){
				return defaultValue;
			}
		}
		public Array<FreemindNode> children() {
			return children;
		}
		public boolean exists() {
			return true;
		}
		public String asString() {
			return text;
		}
		public String asString(String defaultValue) {
			return asString();
		}
		public FreemindNode first() {
			return child(0);
		}
		public int asInt(int defaultValue) {
			try{
				return Integer.parseInt(text);
			}catch(NumberFormatException e){
				return defaultValue;
			}
		}
	}
	
	public FreemindMap parse(FileHandle file) throws IOException {
		FreemindPullParser parser = new FreemindPullParser();
		parser.parse(file);
		return parser.map;
	}
	
}
