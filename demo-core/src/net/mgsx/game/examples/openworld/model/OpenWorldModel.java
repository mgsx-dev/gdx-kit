package net.mgsx.game.examples.openworld.model;

import java.io.IOException;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

import net.mgsx.game.core.helpers.ArrayHelper;
import net.mgsx.game.examples.openworld.utils.FreeMindReader;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindMap;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindNode;

public class OpenWorldModel {
	
	private static FreemindMap map;
	static Array<FreemindNode> elements;
	static float maxRarity;
	private static ObjectMap<Compound, ElementTemplate> fusions = new ObjectMap<Compound, ElementTemplate>();
	
	public static void load(){
		try {
			map = new FreeMindReader().parse(Gdx.files.internal("openworld/openworld-model.mm"));
		} catch (IOException e) {
			throw new GdxRuntimeException(e);
		}
		
		elements = map.root().child("elements").children();
		float rank = 0;
		for(FreemindNode e : elements){
			rank += e.property("rarity").asFloat(1);
		}
		maxRarity = rank;
		elements.sort(new Comparator<FreemindNode>() {

			@Override
			public int compare(FreemindNode o1, FreemindNode o2) {
				float r1 = o1.property("rarity").asFloat(1);
				float r2 = o2.property("rarity").asFloat(1);
				return Float.compare(r1, r2);
			}
		});
		
		for(FreemindNode recipe : map.root().child("recipes").children()){
			FreemindNode deps = recipe.child("fusion").child("deps");
			if(deps.exists()){
				ElementTemplate template = new ElementTemplate();
				template.id = recipe.asString();
				template.compound = new Compound();
				for(FreemindNode dep : deps.children()){
					String depName = dep.asString();
					int required = dep.first().asInt(0);
					template.compound.add(depName, required);
				}
				fusions.put(template.compound, template);
			}
		}
	}
	
	public static String questSummary(String id){
		return map.root().child("quests").child(id).child(0).asString();
	}
	
	public static Array<String> getAllTypes() {
		Array<String> l = new Array<String>();
		for(FreemindNode c : map.root().child("recipes").children()){
			l.add(c.asString());
		}
		return l;
	}
	
	public static ElementTemplate findFusion(Compound compound){
		return fusions.get(compound);
	}
	
	private static FreemindNode binSearch(Array<FreemindNode> array, float v){
		return binSearch(array, v, 0, array.size);
	}
	private static FreemindNode binSearch(Array<FreemindNode> array, float v, int a, int b){
		if(a == b) return array.get(a);
		FreemindNode o1 = array.get(a);
		FreemindNode o2 = array.get(b-1);
		float ra = o1.property("rarity").asFloat(1);
		float rb = o2.property("rarity").asFloat(1);
		if(v < ra) return o1;
		if(v > rb) return o2;
		if(ra == rb){
			a = b = (a+b)/2;
		}else{
			float nv = (v - ra) / (rb - ra);
			int index = (int)MathUtils.lerp(a, b-1, nv);
			float mv = array.get(index).property("rarity").asFloat(1);
			if(v < mv){
				b = index;
			}else{
				a = index+1;
			}
		}
		return binSearch(array, v, a, b);
	}
	
	/**
	 * generate an element based on its seed
	 */
	public static OpenWorldElement generateNewElement(long seed){
		
		OpenWorldElement owElement = new OpenWorldElement();
		
		RandomXS128 rnd = new RandomXS128(seed);
		float rarity = rnd.nextFloat();
		
		FreemindNode element = binSearch(elements, rarity * maxRarity);
		owElement.seed = rnd.nextLong();
		owElement.rarity = element.property("rarity").asFloat(1);
				
		float size1 = element.child("size").child(0).asFloat(1);
		float size2 = element.child("size").child(1).asFloat(size1);
		
		owElement.size = MathUtils.lerp(size1, size2, rnd.nextFloat());

		
		owElement.geo_x = MathUtils.lerp(0.1f, 0.9f, rnd.nextFloat());
		owElement.geo_y = MathUtils.lerp(0.1f, 0.9f, rnd.nextFloat());
		
		owElement.color = generateColor(element.child("color"));
		
		owElement.type = elements.get((int)(elements.size * rnd.nextFloat())).asString();
		
		return owElement;
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
		Array<FreemindNode> names = map.root().child("entities").child(targetName).child("with").child(toolName).children();
		
		// no cases available
		if(names != null) return null;
		
		// pickup random item
		String name = ArrayHelper.any(names).asString();
		
		// find item
		FreemindNode item = map.root().child("elements").child(name);
		
		// generate
		return generateElement(item);
	}

	private static OpenWorldElement generateElement(FreemindNode item) 
	{
		OpenWorldElement element = new OpenWorldElement();
		
		// generates color
		element.color = generateColor(item.child("color"));
		
		// generates shape
		final float ratio = .2f;
		String shape = item.child("shape").first().asString();
		
		element.geo_x = element.geo_y = 1;
		element.type = "box";
		
		if("box".equals(shape)){
		}else if("ball".equals(shape)){
			element.type = "sphere";
		}else if("tube".equals(shape)){
			element.geo_x = element.geo_y = ratio;
		}else if("plate".equals(shape)){
			element.geo_x = ratio;
		}else{
			// TODO find blender model
		}
		
		element.name = item.asString();
		System.out.println(element.name); // XXX
		System.out.println(element.color); // XXX
				
		element.dynamic = true;
		
		element.size = generateSize(item.child("size"));
		
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
		return MathUtils.lerp(f1, f2, MathUtils.random());
	}

	public static String description(String name) {
		// find in any : elements or entities
		FreemindNode item = map.root().child("elements").child(name);
		if(!item.exists()){
			item = map.root().child("entities").child(name);
		}
		if(!item.exists()){
			return "Unknow item " + name;
		}
		return item.child("name").asString(name) + " : " + item.child("hint").asString("no specific info");
	}
	
}
