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

import net.mgsx.game.core.annotations.Incubation;
import net.mgsx.game.examples.openworld.utils.FreeMindReader;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindMap;
import net.mgsx.game.examples.openworld.utils.FreeMindReader.FreemindNode;

public class OpenWorldModel {
	
	public static FreemindMap map;
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
		float h1 = element.child("color").child("hue").child(0).asFloat(0);
		float h2 = element.child("color").child("hue").child(1).asFloat(h1);
		float s1 = element.child("color").child("sat").child(0).asFloat(0);
		float s2 = element.child("color").child("sat").child(1).asFloat(s1);
		float l1 = element.child("color").child("lum").child(0).asFloat(100);
		float l2 = element.child("color").child("lum").child(1).asFloat(l1);
		
		float h = MathUtils.lerp(h1, h2, rnd.nextFloat());
		float s = MathUtils.lerp(s1, s2, rnd.nextFloat());
		float l = MathUtils.lerp(l1, l2, rnd.nextFloat());
		
		owElement.color = HSV_to_RGB(h,s,l);
		
		owElement.type = elements.get((int)(elements.size * rnd.nextFloat())).asString();
		
		return owElement;
	}
	
	@Incubation("missing in libgdx")
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

	
}
