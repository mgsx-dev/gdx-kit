package net.mgsx.game.examples.openworld.model;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.examples.openworld.model.OpenWorldElement.GeometryType;
import net.mgsx.game.examples.openworld.utils.FreeMindReader;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindMap;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindNode;

public class OpenWorldModel {
	
	private static final RandomXS128 inGameRandom = new RandomXS128();
	private static final RandomXS128 rand = new RandomXS128();
	
	private static FreemindMap map;
	
	public static String lang = "fr"; // TODO set it at game startup
	
	public static void load(){
		try {
			map = new FreeMindReader().parse(Gdx.files.internal("openworld/openworld-model.mm"));
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}
	
	public static String questSummary(String id){
		return i18n(map.root().child("quests").child(id), "summary");
	}
	
	public static Array<String> getAllTypes() {
		Array<String> l = new Array<String>();
		for(FreemindNode c : map.root().child("items").children()){
			l.add(c.asString());
		}
		return l;
	}
	
	public static String findFusion(Compound compound) 
	{
		for(FreemindNode node : map.root().child("items").children()){
			FreemindNode buildable = node.child("buildable");
			if(buildable.exists()){
				
				Compound c = new Compound();
				for(FreemindNode compo : buildable.child("deps").children()){
					c.add(compo.asString(), compo.first().asInt(1));
				}
				if(compound.equals(c)){
					return node.asString();
				}
			}
		}
		return null;
	}
	
	// TODO pullup to LibGDX
	/** Converts HSV color sytem to RGB
	 * 
	 * @return RGB values in Libgdx Color class */
	public static Color HSV_to_RGB (float h, float s, float v) {
		int r, g, b;
		int i;
		float f, p, q, t;
		h = (float)Math.max(0.0, Math.min(360.0, h));
		s = (float)Math.max(0.0, Math.min(100.0, s));
		v = (float)Math.max(0.0, Math.min(100.0, v));
		s /= 100;
		v /= 100;

		h /= 60;
		i = (int)Math.floor(h);
		f = h - i;
		p = v * (1 - s);
		q = v * (1 - s * f);
		t = v * (1 - s * (1 - f));
		switch (i) {
		case 0:
			r = Math.round(255 * v);
			g = Math.round(255 * t);
			b = Math.round(255 * p);
			break;
		case 1:
			r = Math.round(255 * q);
			g = Math.round(255 * v);
			b = Math.round(255 * p);
			break;
		case 2:
			r = Math.round(255 * p);
			g = Math.round(255 * v);
			b = Math.round(255 * t);
			break;
		case 3:
			r = Math.round(255 * p);
			g = Math.round(255 * q);
			b = Math.round(255 * v);
			break;
		case 4:
			r = Math.round(255 * t);
			g = Math.round(255 * p);
			b = Math.round(255 * v);
			break;
		default:
			r = Math.round(255 * v);
			g = Math.round(255 * p);
			b = Math.round(255 * q);
		}

		return new Color(r / 255.0f, g / 255.0f, b / 255.0f, 1);
	}

	public static OpenWorldElement useTool(String toolName, String targetName) 
	{
		// find all cases
		Array<FreemindNode> names = map.root().child("items").child(targetName).child("actions").child(toolName).children();
		
		// no cases available
		if(names == null || names.size == 0) return null;
		
		// pickup random item
		String name = ArrayHelper.any(names).asString();
		
		// find item
		FreemindNode item = map.root().child("items").child(name);
		
		// generate
		return generateNewElement(item);
	}

	public static OpenWorldElement generateNewElement(String UID) 
	{
		return generateNewElement(map.root().child("items").child(UID));
	}
	private static OpenWorldElement generateNewElement(FreemindNode item) 
	{
		long seed = inGameRandom.nextLong();
		return generateElement(item, seed);
	}
	public static void generateElement(OpenWorldElement element) 
	{
		generateElement(element, map.root().child("items").child(element.type));
	}
	private static void generateElement(OpenWorldElement element, FreemindNode item) 
	{
		// Warning : order is very important here in order to generate same object.
		// TODO maybe use layers
		rand.setSeed(element.seed);

		FreemindNode procedural = item.child("procedural");
		if(procedural.exists()){
			// generates color
			element.color = generateColor(procedural.child("color"));
			
			// generates shape
			final float ratio = .2f;
			String shape = procedural.child("shape").first().asString();
			
			// default is box
			element.geo_x = element.geo_y = 1;
			element.shape = GeometryType.BOX;
			
			if("ball".equals(shape)){
				element.shape = GeometryType.SPHERE;
			}else if("tube".equals(shape)){
				element.geo_x = element.geo_y = ratio;
			}else if("plate".equals(shape)){
				element.geo_x = ratio;
			}
			
			element.size = generateSize(procedural.child("size"));
		}
	}
	private static OpenWorldElement generateElement(FreemindNode item, long seed) 
	{
		OpenWorldElement element = new OpenWorldElement();
		element.seed = seed;
		element.type = item.asString();
		element.dynamic = !item.child("static").exists();
		
		generateElement(element);
		
		return element;
	}

	private static float generateSize(FreemindNode size) {
		return generateFloat(size, 1);
	}

	private static Color generateColor(FreemindNode color) 
	{
		float h = generateFloat(color.child("hue"), 0);
		float s = generateFloat(color.child("sat"), 50);
		float l = generateFloat(color.child("lum"), 50);
		
		return HSV_to_RGB(h,s,l);
	}

	private static float generateFloat(FreemindNode node, float defaultValue) {
		float f1 = node.child(0).asFloat(defaultValue);
		float f2 = node.child(1).asFloat(f1);
		return MathUtils.lerp(f1, f2, rand.nextFloat());
	}

	public static String description(String type) {
		return i18n(map.root().child("items").child(type), "hint");
	}

	private static String i18n(FreemindNode node, String field) {
		return i18n(node, field, defaultString(field));
	}
	private static String i18n(FreemindNode node, String field, String defaultValue) {
		// return defulat value if field doesn't exists.
		FreemindNode fieldNode = node.child(field);
		if(!fieldNode.exists()) return defaultValue;
		// return first translation if theres is not translation for the current lang.
		FreemindNode langNode = fieldNode.child(lang);
		if(!langNode.exists()) return fieldNode.first().first().asString(defaultValue);
		// return the found translation
		return langNode.first().asString(defaultValue);
	}

	public static OpenWorldElement generateNewGarbageElement(Compound compound) 
	{
		// TODO based on compound ?
		return generateNewElement("dirt");
	}

	public static String name(String type) {
		return i18n(map.root().child("items").child(type), "name", missing(type));
	}

	private static String missing(String text) {
		return "[" + text + "]";
	}

	private static String defaultString(String name) {
		return i18n(map.root().child("default"), name, missing(name));
	}

	public static Integer space(String type) {
		FreemindNode transportNode = map.root().child("items").child(type).child("transportable");
		if(transportNode.exists()){
			return transportNode.first().asInt(1);
		}
		return null;
	}

	public static Integer energy(String type) {
		FreemindNode eatNode = map.root().child("items").child(type).child("eatable");
		if(eatNode.exists()){
			return eatNode.first().asInt(1);
		}
		return null;
	}

	public static Integer sleepableEnergy(String type) {
		FreemindNode node = map.root().child("items").child(type).child("sleepable");
		if(node.exists()){
			return node.first().asInt(1);
		}
		return null;
	}

}
