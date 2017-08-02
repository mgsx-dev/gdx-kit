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
	
	public static void load(){
		try {
			map = new FreeMindReader().parse(Gdx.files.internal("openworld/openworld-model.mm"));
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
	}
	
	public static String questSummary(String id){
		return map.root().child("quests").child(id).child(0).asString();
	}
	
	public static Array<String> getAllTypes() {
		Array<String> l = new Array<String>();
		for(FreemindNode c : map.root().child("items").children()){
			l.add(c.asString());
		}
		return l;
	}
	
	public static ElementTemplate findFusion(Compound compound) 
	{
		for(FreemindNode node : map.root().child("items").children()){
			FreemindNode buildable = node.child("buildable");
			if(buildable.exists()){
				
				Compound c = new Compound();
				for(FreemindNode compo : buildable.child("deps").children()){
					compound.add(compo.asString(), compo.first().asInt(1));
				}
				if(compound.equals(c)){
					ElementTemplate t = new ElementTemplate();
					t.compound = c;
					t.id = node.asString();
					return t;
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
		element.dynamic = true;
		
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

	public static String description(String name) {
		// find in any : elements or entities
		FreemindNode item = map.root().child("items").child(name);
		if(!item.exists()){
			return "Unknow item " + name;
		}
		return item.child("name").first().asString(name) + " : " + item.child("hint").first().asString("no specific info");
	}

	public static OpenWorldElement generateNewGarbageElement(Compound compound) 
	{
		// TODO based on compound ?
		return generateNewElement("dirt");
	}

	public static String name(String type) {
		return map.root().child("items").child("name").asString("Something");
	}

}
